package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.entity.ArticleComment;
import com.cmc.entity.UserLikeRecord;
import com.cmc.enums.type.LikeTypeEnum;
import com.cmc.mapper.ArticleCommentMapper;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.UserLikeRecordMapper;
import com.cmc.rocketmq.message.LikeMessage;
import com.cmc.service.UserLikeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2025-10-18
 */
@Service
public class UserLikeRecordServiceImpl extends ServiceImpl<UserLikeRecordMapper, UserLikeRecord> implements UserLikeRecordService {

    @Autowired
    private UserLikeRecordMapper userLikeRecordMapper;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleCommentMapper articleCommentMapper;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public R syncLikeRecord(UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType) {
        //  查询数据库中是否有这条数据
        UserLikeRecord existingRecord = userLikeRecordMapper.selectOne(new QueryWrapper<UserLikeRecord>()
                .eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));
        if (existingRecord != null) {
            // 判断is_deleted
            if ("1".equals(existingRecord.getIsDeleted())){
                existingRecord.setIsDeleted("0");
            }else{
                existingRecord.setIsDeleted("1");
            }
            // 修改记录
            userLikeRecordMapper.updateById(existingRecord);
        }else{
            // 这时候没有这条记录 添加记录
            userLikeRecord.setUserId(userId);
            userLikeRecord.setTargetId(targetId);
            userLikeRecord.setTargetType(targetType);
            userLikeRecord.setStatus("0");
            userLikeRecord.setIsDeleted("0");
            userLikeRecordMapper.insert(userLikeRecord);
        }

        // 2. 修改 表中记录的条数
        LikeTypeEnum typeEnum = LikeTypeEnum.fromCode(targetType);
        if (typeEnum != null) {
            switch (typeEnum) {
                case ARTICLE:
                    Article article = articleMapper.selectOne(new QueryWrapper<Article>().eq("id", targetId));
                    int likeCount = 0;
                    likeCount = userLikeRecordMapper.selectCount(new QueryWrapper<UserLikeRecord>()
                            .eq("target_id",targetId)
                            .eq("target_type", targetType)
                            .eq("is_deleted","0"));
                    article.setLikeCount(likeCount);
                    articleMapper.updateById(article);
                    break;
                case VIDEO:
                    System.out.println("处理视频点赞");
                    break;
                case ARTICLE_COMMENT:
                    ArticleComment articleComment = articleCommentMapper.selectOne(new QueryWrapper<ArticleComment>()
                            .eq("id", targetId));
                    if (articleComment != null) {
                        // 计算当前 评论的点赞数 并且赋值给comment
                        Integer count = userLikeRecordMapper.selectCount(new QueryWrapper<UserLikeRecord>()
                                .eq("target_id", targetId)
                                .eq("target_type", targetType)
                                .eq("is_deleted", "0"));
                        articleComment.setLikeCount(count);
                        articleCommentMapper.updateById(articleComment);
                    }
                    System.out.println("处理这篇文章评论的点赞");
                    break;
                default:
                    break;
            }
        }

        return R.ok("success");
    }

    @Override
    public R insertUserLikeRecord(UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType) {

        String userKey = "article:like:users:" + targetId;
        String countKey = "article:like:count:" + targetId;


        Boolean liked = redisTemplate.opsForSet().isMember(userKey, userId);

        if (Boolean.TRUE.equals(liked)) {
            //去掉点赞
            redisTemplate.opsForSet().remove(userKey,userId);
            redisTemplate.opsForValue().decrement(countKey);

            // 发送消息
            rocketMQTemplate.convertAndSend(
                    "article-like-topic",
                    new LikeMessage(targetId,targetType,userId,"UNLIKE")
            );

            return R.ok("取消点赞");

        }else{
            //点赞
            redisTemplate.opsForSet().add(userKey,userId);
            redisTemplate.opsForValue().increment(countKey);

            rocketMQTemplate.convertAndSend(
                    "article-like-topic",
                    new LikeMessage(targetId,targetType,userId,"LIKE")
            );

            return R.ok("点赞");
        }

    }

    @Override
    public R getUserLikeRecord(Long userId, Long targetId, String targetType) {
        List<UserLikeRecord> userLikeRecords = userLikeRecordMapper.selectList(new QueryWrapper<UserLikeRecord>()
                .eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));
        if (!userLikeRecords.isEmpty()) {
            return R.ok("success",userLikeRecords.get(0));
        }
        return R.error("fail");
    }



    @Override
    public R testMQ() {
        SendResult sendResult = rocketMQTemplate.syncSend("test-topic", "我是一个同步消息");
        System.out.println(sendResult.getSendStatus());
        System.out.println(sendResult.getMsgId());
        System.out.println(sendResult.getMessageQueue());
        return null;
    }
}
