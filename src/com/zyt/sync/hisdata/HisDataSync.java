package com.zyt.sync.hisdata;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.hisdata.HisDatas;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Administrator on 2015/10/4.
 */
public class HisDataSync {
    private static final Logger logger = LogManager.getLogger(HisDataSync.class);
    private ArrayList<HisDataSyncRecord> recordList = new ArrayList<HisDataSyncRecord>();

    public HisDataSync(Connection conn) {
        Calendar curCalendar = Calendar.getInstance();
        curCalendar.set(Calendar.HOUR_OF_DAY, 0);
        curCalendar.set(Calendar.MINUTE, 0);
        curCalendar.set(Calendar.SECOND, 0);
        curCalendar.set(Calendar.MILLISECOND, 0);

        Calendar startCalendar = (Calendar)curCalendar.clone();
        startCalendar.add(Calendar.DAY_OF_MONTH, -1);

        Statement stmt = null;
        ResultSet rs = null;

        int count = 0;
        String queryStmt = String.format(SqlStmts.DATA_SYNC_READ,
                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                curCalendar.get(Calendar.YEAR), curCalendar.get(Calendar.MONTH) + 1, curCalendar.get(Calendar.DAY_OF_MONTH),
                BootstrapServlet.getRequiredSignalsForHisData());
        logger.debug("queryStmt is " + queryStmt);
        try {
            logger.debug("start querying history data to sync");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                recordList.add(new HisDataSyncRecord(rs.getInt(1), rs.getString(2), rs.getInt(3),
                        rs.getString(4), rs.getInt(5), rs.getString(6), rs.getString(7), rs.getString(8)));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying history data to sync, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public void doSync(String oracleConnUrl) {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(oracleConnUrl);
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            logger.error("failed to sync history data to Oracle");
            Util.safeClose(stmt, conn);
            return;
        }

        logger.debug("start to sync history data to Oracle");
        for (HisDataSyncRecord record : recordList) {
            String insertStmt = String.format(SqlStmts.DATA_SYNC_WRITE,
                    record.getSiteId(), record.getSiteName(),
                    record.getDeviceId(), record.getDeviceName(),
                    record.getSignalNo(), record.getSignalName(),
                    record.getDataValue(), record.getTimeStamp());
            try {
                stmt.addBatch(insertStmt);
            } catch (SQLException e) {
            }
        }

        try {
            stmt.executeBatch();
        } catch (SQLException e) {
            logger.error("failed to sync history data to Oracle");
        } finally {
            try {
                conn.commit();
            } catch (SQLException e) {
            }
            Util.safeClose(stmt, conn);
        }
        logger.debug("end of sync history data to Oracle");
    }
}
