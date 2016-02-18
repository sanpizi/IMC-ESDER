package com.zyt.job.test;

import com.zyt.job.ZYTScheduler;
import org.quartz.SchedulerException;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/2.
 */
public class JobTest {
    public static void main(String[] args) throws SchedulerException, InterruptedException {
        ZYTScheduler scheduler = new ZYTScheduler();

        Thread.sleep(1000 * 20);

        scheduler.shutdown();
        System.out.println("Done");
    }
}
