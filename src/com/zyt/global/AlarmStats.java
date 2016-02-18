package com.zyt.global;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.alarm.RealTimeAlarms;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Created by Administrator on 2015/8/11.
 */
public class AlarmStats {
    private static final Logger logger = LogManager.getLogger(AlarmStats.class);

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getFatal() {
        return fatal;
    }

    public void setFatal(int fatal) {
        this.fatal = fatal;
    }

    public int getUrgent() {
        return urgent;
    }

    public void setUrgent(int urgent) {
        this.urgent = urgent;
    }

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    public int getGeneral() {
        return general;
    }

    public void setGeneral(int general) {
        this.general = general;
    }

    public void totalize() {
        total = fatal + urgent + important + general;
    }

    private int total;
    private int fatal;
    private int urgent;
    private int important;
    private int general;

    public AlarmStats(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;

        String queryStr = String.format(SqlStmts.GLOBALSTATS_ALARM, BootstrapServlet.getRequiredTypeIdsForAlarm());
        try {
            stmt = conn.createStatement();
            logger.debug("start querying globalStats.alarm");
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                switch (rs.getInt(2)) {
                    case 1:
                        general = rs.getInt(1);
                        break;
                    case 2:
                        important = rs.getInt(1);
                        break;
                    case 3:
                        urgent = rs.getInt(1);
                        break;
                    case 4:
                        fatal = rs.getInt(1);
                        break;
                    case 0:
                    default:
                        //do nothing
                }
            }
            this.totalize();
            logger.debug("end of querying globalStats.alarm");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public AlarmStats(Connection conn, int siteId) {
        Statement stmt = null;
        ResultSet rs = null;

        String queryStr = String.format(SqlStmts.SITE_DETAILS_ALARMSTATS, BootstrapServlet.getRequiredTypeIdsForAlarm(), siteId);
        try {
            logger.debug("start querying siteDetail.alarmStats");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                switch (rs.getInt(2)) {
                    case 1:
                        general = rs.getInt(1);
                        break;
                    case 2:
                        important = rs.getInt(1);
                        break;
                    case 3:
                        urgent = rs.getInt(1);
                        break;
                    case 4:
                        fatal = rs.getInt(1);
                        break;
                    case 0:
                    default:
                        //do nothing
                }
            }
            this.totalize();
            logger.debug("end of querying siteDetail.alarmStats");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
