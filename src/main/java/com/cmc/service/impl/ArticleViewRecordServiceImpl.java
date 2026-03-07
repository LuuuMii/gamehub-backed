package com.cmc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.common.R;
import com.cmc.entity.Article;
import com.cmc.entity.ArticleUserBehavior;
import com.cmc.entity.ArticleViewRecord;
import com.cmc.enums.ResultCodeEnum;
import com.cmc.enums.article.UserBehaviorType;
import com.cmc.mapper.ArticleMapper;
import com.cmc.mapper.ArticleUserBehaviorMapper;
import com.cmc.mapper.ArticleViewRecordMapper;
import com.cmc.service.ArticleViewRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cmc.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author C
 * @since 2026-03-06
 */
@Service
@Slf4j
public class ArticleViewRecordServiceImpl extends ServiceImpl<ArticleViewRecordMapper, ArticleViewRecord> implements ArticleViewRecordService {

    @Autowired
    private ArticleViewRecordMapper articleViewRecordMapper;
    @Autowired
    private ArticleUserBehaviorMapper articleUserBehaviorMapper;;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private RedisUtil redisUtil;


    /**
     * 用户观看帖子记录(30分钟内只记录一条数据)
     * @param record
     * @return
     */
    @Override
    @Transactional
    public R addViewRecord(ArticleViewRecord record) {
        //1.查看redis中 30分钟内是否有相同的记录  如果没有 则添加
        String redisKey = "articleViewRecord:" + record.getUserId() + ":" + record.getArticleId();
        // 30分钟过期操作
        Boolean success = redisUtil.setIfAbsent(redisKey,"1",30L, TimeUnit.MINUTES);
        if (Boolean.TRUE.equals(success)){
            // 写入数据库操作
            articleViewRecordMapper.insert(record);

            // 修改文章表中的viewcount
            articleMapper.increaseViewCount(record.getArticleId());

            // 记录用户行为
            if(!ObjectUtils.isEmpty(record.getUserId())){
                ArticleUserBehavior behavior = new ArticleUserBehavior();
                behavior.setArticleId(record.getArticleId());
                behavior.setUserId(record.getUserId());
                behavior.setBehaviorType(UserBehaviorType.VIEW_BEHAVIOR.getType());
                behavior.setScore(UserBehaviorType.VIEW_BEHAVIOR.getScore());
                try{
                    articleUserBehaviorMapper.insert(behavior);
                }catch (DuplicateKeyException e){
                    log.info("用户行为已存在 userId={} articleId={}",
                            behavior.getUserId(), behavior.getArticleId());
                }
            }

        }
        return R.ok("success");
    }
}
