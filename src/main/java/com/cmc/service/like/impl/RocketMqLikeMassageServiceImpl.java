package com.cmc.service.like.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.common.R;
import com.cmc.entity.UserLikeRecord;
import com.cmc.mapper.UserLikeRecordMapper;
import com.cmc.rocketmq.message.LikeMessage;
import com.cmc.service.like.LikeMassageService;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "project.mq.enable",
        havingValue = "true"
)
public class RocketMqLikeMassageServiceImpl extends ServiceImpl<UserLikeRecordMapper, UserLikeRecord> implements LikeMassageService {


    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    // 发送mq消息
    @Override
    public R insertLikeRecord(Boolean liked, UserLikeRecord userLikeRecord, Long userId, Long targetId, String targetType) {

        String userKey = "article:like:users:" + targetId;
        String countKey = "article:like:count:" + targetId;
        if (Boolean.TRUE.equals(liked)) {
            //去掉点赞
            redisTemplate.opsForSet().remove(userKey, userId);
            redisTemplate.opsForValue().decrement(countKey);

            // 发送消息
            rocketMQTemplate.convertAndSend(
                    "article-like-topic",
                    new LikeMessage(targetId, targetType, userId, "UNLIKE")
            );

            return R.ok("取消点赞");

        } else {
            //点赞
            redisTemplate.opsForSet().add(userKey, userId);
            redisTemplate.opsForValue().increment(countKey);

            rocketMQTemplate.convertAndSend(
                    "article-like-topic",
                    new LikeMessage(targetId, targetType, userId, "LIKE")
            );

            return R.ok("点赞");
        }
    }


}
