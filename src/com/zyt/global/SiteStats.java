package com.zyt.global;

import java.sql.*;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.alarm.RealTimeAlarms;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/8/11.
 */
public class SiteStats {
    private static final Logger logger = LogManager.getLogger(SiteStats.class);

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getNormal() {
        return normal;
    }

    public void setNormal(int normal) {
        this.normal = normal;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getAlarm() {
        return alarm;
    }

    public void setAlarm(int alarm) {
        this.alarm = alarm;
    }

    public void totalize() {
        total = normal + offline + alarm;
    }

    private int total;
    private int normal;
    private int offline;
    private int alarm;

    public SiteStats(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;

        String queryStr = String.format(SqlStmts.GLOBALSTATS_SITE, BootstrapServlet.getSiteOnlineInternalInMinutes(),
                BootstrapServlet.getRequiredTypeIdsForAlarm());
        logger.debug("queryStr is " + queryStr);
        try {
            stmt = conn.createStatement();
            logger.debug("start querying globalStats.sites");
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                String status = rs.getString(2);
                String alarmFlag = rs.getString(3);
                if (status.equals("Normal")) {
                    if (alarmFlag.equals("true")) {
                        alarm++;
                    } else {
                        normal++;
                    }
                } else {
                    offline++;
                }
            }
            this.totalize();
            logger.debug("end of querying globalStats.sites");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
