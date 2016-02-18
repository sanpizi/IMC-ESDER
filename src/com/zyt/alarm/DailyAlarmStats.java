package com.zyt.alarm;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Administrator on 2015/8/8.
 */
public class DailyAlarmStats {
    private static final Logger logger = LogManager.getLogger(DailyAlarmStats.class);

    public ArrayList<Long> getDatetime() {
        return datetime;
    }

    public ArrayList<Integer> getFatal() {
        return fatal;
    }

    public ArrayList<Integer> getUrgent() {
        return urgent;
    }

    public ArrayList<Integer> getImportant() {
        return important;
    }

    public ArrayList<Integer> getGeneral() {
        return general;
    }

    private ArrayList<Long> datetime = new ArrayList<Long>();
    private ArrayList<Integer> fatal = new ArrayList<Integer>();
    private ArrayList<Integer> urgent = new ArrayList<Integer>();
    private ArrayList<Integer> important = new ArrayList<Integer>();
    private ArrayList<Integer> general = new ArrayList<Integer>();

    // date parameters : yyyy-mm-dd
    //startDate included, endDate excluded
    public DailyAlarmStats(String startDate, String endDate, Connection conn) {
        doQuery(startDate, endDate, conn, null);
    }

    public DailyAlarmStats(long startDate, long endDate, Connection conn) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        logger.debug("starttime: " + formatter.format( new Date(startDate)));
        logger.debug("endtime: " + formatter.format( new Date(endDate)));
        doQuery(formatter.format(new Date(startDate)), formatter.format(new Date(endDate)), conn, null);
    }

    private void doQuery(String startDate, String endDate, Connection conn, SimpleDateFormat formatter) {
        Statement stmt = null;
        ResultSet rs = null;
        String queryStr = String.format(SqlStmts.SELECT_DAILY_ALARM_STATS,
                BootstrapServlet.getRequiredTypeIdsForAlarm(), startDate, endDate);
        logger.debug("queryStr is " + queryStr);
        try {
            logger.debug("start querying daily alarm stats");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStr);
            if (formatter == null) {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
            }
            while (rs.next()) {
                datetime.add(formatter.parse(rs.getString(1)).getTime());
                fatal.add(rs.getInt(2));
                urgent.add(rs.getInt(3));
                important.add(rs.getInt(4));
                general.add(rs.getInt(5));

            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying daily alarm stats");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
