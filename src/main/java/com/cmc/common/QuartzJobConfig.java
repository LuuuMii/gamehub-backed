package com.cmc.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.Job;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuartzJobConfig {
    private String jobName;
    private String jobGroup;
    private String cron;
    private Class<? extends Job> jobClass;
}
