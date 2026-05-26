package com.cmc.rocketmq.consumer;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.entity.Article;
import com.cmc.entity.ArticleUserBehavior;
import com.cmc.entity.UserLikeRecord;
import com.cmc.enums.article.UserBehaviorType;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.ArticleUserBehaviorMapper;
import com.cmc.mapper.UserLikeRecordMapper;
import com.cmc.rocketmq.message.LikeMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@RocketMQMessageListener(
        topic = "article-like-topic",
        consumerGroup = "like-consumer"
)
@Component
@ConditionalOnProperty(
        name = "project.mq.enable",
        havingValue = "true"
)
public class LikeConsumer implements RocketMQListener<LikeMessage> {

    @Autowired
    private UserLikeRecordMapper userLikeRecordMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleUserBehaviorMapper articleUserBehaviorMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    @Transactional
    public void onMessage(LikeMessage msg) {

        Long userId = msg.getUserId();
        Long targetId = msg.getTargetId();
        String targetType = msg.getTargetType();
        // 数据库操作
        // 查询数据库是否有这条记录
        UserLikeRecord userLikeRecord = userLikeRecordMapper.selectOne(new QueryWrapper<UserLikeRecord>()
                .eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));

        // 点赞记录表数据
        if (!ObjectUtils.isEmpty(userLikeRecord)){
            // 存在这条数据
            if ("UNLIKE".equals(msg.getAction())){
                // 修改状态 is_delete 为 1
                userLikeRecord.setIsDeleted("1");
            }else if ("LIKE".equals(msg.getAction())){
                userLikeRecord.setIsDeleted("0");
            }
            userLikeRecordMapper.updateById(userLikeRecord);
        }
        else{
            // 不存在这条数据
            UserLikeRecord newData = new UserLikeRecord();
            newData.setUserId(userId);
            newData.setTargetId(targetId);
            newData.setTargetType(targetType);
            if ("UNLIKE".equals(msg.getAction())){
                // 添加数据
                newData.setIsDeleted("1");
            }else if ("LIKE".equals(msg.getAction())){
                newData.setIsDeleted("0");
            }
            userLikeRecordMapper.insert(newData);
        }

        // 文章表 点赞数更改
        String countKey = "article:like:count:" + targetId;
        Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
        Article article = new Article();
        article.setId(targetId);
        article.setLikeCount(count);
        articleMapper.updateById(article);

        // 用户行为表
        if ("LIKE".equals(msg.getAction())){
            ArticleUserBehavior behavior = new ArticleUserBehavior();
            behavior.setArticleId(targetId)
                            .setUserId(userId)
                                    .setBehaviorType(UserBehaviorType.LIKE_BEHAVIOR.getType())
                                            .setScore(UserBehaviorType.LIKE_BEHAVIOR.getScore());
            articleUserBehaviorMapper.insert(behavior);
        }else if("UNLIKE".equals(msg.getAction())){
            articleUserBehaviorMapper.delete(new QueryWrapper<ArticleUserBehavior>()
                    .eq("user_id",userId)
                    .eq("article_id",targetId)
                    .eq("behavior_type",UserBehaviorType.LIKE_BEHAVIOR.getType()));
        }

        System.out.println("LikeConsumer:" + msg.getTargetId() + "success");

    }
}
