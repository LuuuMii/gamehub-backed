package com.cmc.quartz.article;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cmc.constans.article.article.ArticleStatusConstant;
import com.cmc.entity.Article;
import com.cmc.mapper.ArticleMapper;
import com.cmc.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ScheduledPublishArticleJob extends QuartzJobBean {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        Date now = new Date();

        // 计算一分钟前的时间
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, -1);
        Date oneMinuteAgo = cal.getTime();

        // 查询当前时间 未发布的所有文章
        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.between("publish_time",oneMinuteAgo, now);
        articleQueryWrapper.eq("status", ArticleStatusConstant.TIMING);
        List<Article> articleList = articleMapper.selectList(articleQueryWrapper);

        for (Article article : articleList) {
            try {
                // 避免并发或重复发布
                RLock lock = redissonClient.getLock("article:publish:" + article.getId());
                if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                    try {
                        // 再查一次，确认数据库中还是未发布状态（防止重复）
                        Article latest = articleMapper.selectById(article.getId());
                        if (ArticleStatusConstant.TIMING.equals(latest.getStatus())) {
                            articleService.publishArticle(latest);
                            log.info("【发布成功】文章ID: {}, 标题: {}", latest.getId(), latest.getTitle());
                        } else {
                            log.info("【跳过】文章ID: {}, 已不是定时状态", latest.getId());
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (Exception e) {
                log.error("【发布异常】文章ID: {}, 异常信息: {}", article.getId(), e.getMessage(), e);
            }
        }


    }
}
