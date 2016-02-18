package com.zyt.job;

import com.zyt.job.jobs.DailyHisDataJob;
import com.zyt.job.jobs.HisAlarmSyncJob;
import com.zyt.job.jobs.HisDataSyncJob;
import com.zyt.job.jobs.MonthlyHisDataJob;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Administrator on 2015/10/2.
 */
public class ZYTScheduler {
    private Scheduler scheduler;
    private static AtomicInteger JOBID = new AtomicInteger(0);

    public ZYTScheduler() throws SchedulerException {
        Properties props = new Properties();
        props.put("org.quartz.scheduler.skipUpdateCheck", "true");
        props.setProperty("org.quartz.threadPool.threadCount", "5");
        scheduler = new StdSchedulerFactory(props).getScheduler();  //disable update check on 2015.10.16
        scheduler.start();
        scheduleJob(DailyHisDataJob.class, "0 30 0 * * ?");  //每天0:30触发
        scheduleJob(MonthlyHisDataJob.class, "0 0 1 1 * ?");  //每月1日1:00触发
        scheduleJob(HisAlarmSyncJob.class, "0 0 23 * * ?");  //每天23时触发
        scheduleJob(HisDataSyncJob.class, "0 0 3 * * ?");  //每天3时触发
    }

    public void shutdown() {
        try {
            scheduler.clear();
        } catch (SchedulerException e) {
        } finally {
            try {
                scheduler.shutdown(true);
            } catch (SchedulerException e) {
            }
        }
    }

    public boolean scheduleJob(Class jobClass, String cronString) {
        int id = JOBID.getAndIncrement();
        JobDetail job = JobBuilder.newJob(jobClass).withIdentity("JOB_" + id).build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("TRIGGER_" + id, "ZYTTriggerGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cronString)).build();
        try {
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            return false;
        }
        return true;
    }
}
