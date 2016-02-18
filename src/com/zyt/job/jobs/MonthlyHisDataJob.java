package com.zyt.job.jobs;

import com.zyt.hisdatastats.monthly.MHisDataStats;
import com.zyt.ws.BootstrapServlet;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;

/**
 * Created by Administrator on 2015/10/4.
 */
public class MonthlyHisDataJob implements Job {
    private static final Logger logger = LogManager.getLogger(MonthlyHisDataJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            MHisDataStats data = new MHisDataStats(conn.getConn());
            data.export();
        } catch (Exception e) {
            logger.error("failed to do monthly history data statistics");
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }

        //remote olds
        File dir = new File(BootstrapServlet.getStatsFilesFolder());
        File[] statsFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                String fileName = pathname.getName();
                return pathname.isFile() && fileName.startsWith("StatisticsDataReport") && fileName.endsWith("xlsx");
            }
        });
        if (statsFiles != null && statsFiles.length > BootstrapServlet.getMonthlyStatsFilesCount()) {
            Arrays.sort(statsFiles);
            for (int i = 0; i < statsFiles.length - BootstrapServlet.getMonthlyStatsFilesCount(); i++) {
                statsFiles[i].delete();
            }
        }
    }
}
