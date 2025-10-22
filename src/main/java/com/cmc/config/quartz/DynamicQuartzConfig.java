package com.cmc.config.quartz;

import com.cmc.common.QuartzJobConfig;
import com.cmc.quartz.article.ScheduledPublishArticleJob;
import com.cmc.quartz.es.ArticleTagSyncJob;
import com.cmc.quartz.es.TestSyncJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



import java.util.Arrays;
import java.util.List;

@Configuration
public class DynamicQuartzConfig {

    @Autowired
    private Scheduler scheduler;

    @Bean
    public void scheduleJobs() throws SchedulerException {
        List<QuartzJobConfig> jobs = Arrays.asList(
                new QuartzJobConfig("ArticleTagJob","es","0 0/30 * * * ?", ArticleTagSyncJob.class),
                new QuartzJobConfig("test","test","0/30 * * * * ?", TestSyncJob.class),
                new QuartzJobConfig("ScheduledPublishArticleJob","article","0 * * * * ?", ScheduledPublishArticleJob.class)
        );
        for (QuartzJobConfig jobConfig : jobs) {
            JobDetail jobDetail = JobBuilder.newJob(jobConfig.getJobClass())
                    .withIdentity(jobConfig.getJobName(), jobConfig.getJobGroup())
                    .storeDurably()
                    .build();
            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(jobConfig.getJobName() + "Trigger", jobConfig.getJobGroup())
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobConfig.getCron()))
                    .build();

            if (!scheduler.checkExists(jobDetail.getKey())){
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }
    }


}
