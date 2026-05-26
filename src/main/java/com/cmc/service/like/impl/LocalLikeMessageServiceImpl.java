package com.cmc.service.like.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.entity.ArticleUserBehavior;
import com.cmc.entity.UserLikeRecord;
import com.cmc.enums.article.UserBehaviorType;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.ArticleUserBehaviorMapper;
import com.cmc.mapper.UserLikeRecordMapper;
import com.cmc.service.like.LikeMassageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@ConditionalOnProperty(
        name = "project.mq.enable",
        havingValue = "false",
        matchIfMissing = true
)
public class LocalLikeMessageServiceImpl extends ServiceImpl<UserLikeRecordMapper, UserLikeRecord> implements LikeMassageService {

    @Autowired
    private UserLikeRecordMapper userLikeRecordMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private ArticleUserBehaviorMapper articleUserBehaviorMapper;

    @Override
    public R insertLikeRecord(Boolean liked, UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType) {
        String action = "0".equals(userLikeRecord.getIsDeleted()) ? "LIKE" : "UNLIKE";

        // 数据库操作
        // 查询数据库是否有这条记录
        UserLikeRecord record = userLikeRecordMapper.selectOne(new QueryWrapper<UserLikeRecord>()
                .eq("user_id", userId)
                .eq("target_id", targetId)
                .eq("target_type", targetType));

        // 点赞记录表数据
        if (!ObjectUtils.isEmpty(record)){
            // 存在这条数据
            if ("UNLIKE".equals(action)){
                // 修改状态 is_delete 为 1
                userLikeRecord.setIsDeleted("1");
            }else if ("LIKE".equals(action)){
                userLikeRecord.setIsDeleted("0");
            }
            userLikeRecordMapper.updateById(record);
        }
        else{
            // 不存在这条数据
            UserLikeRecord newData = new UserLikeRecord();
            newData.setUserId(userId);
            newData.setTargetId(targetId);
            newData.setTargetType(targetType);
            newData.setStatus("0");
            if ("UNLIKE".equals(action)){
                // 添加数据
                newData.setIsDeleted("1");
            }else if ("LIKE".equals(action)){
                newData.setIsDeleted("0");
            }
            userLikeRecordMapper.insert(newData);
        }

        // 文章表 点赞数更改
        String countKey = "article:like:count:" + targetId;
//        Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
        Integer count = userLikeRecordMapper.selectCount(new QueryWrapper<UserLikeRecord>().eq("target_id", targetId)
                .eq("target_type", targetType));
        Article article = new Article();
        article.setId(targetId);
        article.setLikeCount(count);
        articleMapper.updateById(article);

        // 用户行为表
//        if ("LIKE".equals(action)){
//            ArticleUserBehavior behavior = new ArticleUserBehavior();
//            behavior.setArticleId(targetId)
//                    .setUserId(userId)
//                    .setBehaviorType(UserBehaviorType.LIKE_BEHAVIOR.getType())
//                    .setScore(UserBehaviorType.LIKE_BEHAVIOR.getScore());
//            articleUserBehaviorMapper.insert(behavior);
//        }else if("UNLIKE".equals(action)){
//            articleUserBehaviorMapper.delete(new QueryWrapper<ArticleUserBehavior>()
//                    .eq("user_id",userId)
//                    .eq("article_id",targetId)
//                    .eq("behavior_type",UserBehaviorType.LIKE_BEHAVIOR.getType()));
//        }
        return R.ok();
    }
}
