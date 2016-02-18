package com.zyt.job.jobs;

import com.zyt.sync.hisdata.HisDataSync;
import com.zyt.ws.BootstrapServlet;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Created by Administrator on 2015/10/4.
 */
public class HisDataSyncJob implements Job {
    private static final Logger logger = LogManager.getLogger(HisDataSyncJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            HisDataSync data = new HisDataSync(conn.getConn());
            data.doSync(BootstrapServlet.getOracleConnUrl());
        } catch (Exception e) {
            logger.error("failed to sync history data to Oracle");
            return;
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }
    }
}
