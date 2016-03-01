package com.zyt.sitedetail;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.alarm.RealTimeAlarms;
import com.zyt.global.AlarmStats;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/12.
 */
public class SiteDetail {
    private static final Logger logger = LogManager.getLogger(SiteDetail.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public AlarmStats getAlarmStats() {
        return alarmStats;
    }

    public void setAlarmStats(AlarmStats alarmStats) {
        this.alarmStats = alarmStats;
    }

    public int getX_pos() {
        return x_pos;
    }

    public void setX_pos(int x_pos) {
        this.x_pos = x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<SignalRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<SignalRecord> recordList) {
        this.recordList = recordList;
    }

    private int id;
    private String name;
    private String status;
    private int areaId;
    private String areaName;
    private int x_pos;
    private int y_pos;
    private AlarmStats alarmStats;
    private int totalRecords;
    private ArrayList<SignalRecord> recordList = new ArrayList<SignalRecord>();

    //public static final String REQUIRED_SIGNALS = "957,958,959,960,961,962,976,977,978,979,980,981,982,983,984,985,986,987,989,990,991,992,1006,1008,1011,1014,1015,1019";

    public SiteDetail(Connection conn, int id) {
        this.id = id;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            logger.debug("start querying site's brief recordList");
            stmt = conn.createStatement();

            String briefQuery = String.format(SqlStmts.SITE_DETAILS_BRIEF, id);
            logger.debug("query string is " + briefQuery);
            rs = stmt.executeQuery(briefQuery);
            if (rs.next()) {
                this.name = rs.getString(2);
                this.status = "Offline";
                switch (rs.getInt(3)) {
                    case 1:
                        this.status = "Alarm";
                        break;
                    case 0:
                        this.status = "Normal";
                        break;
                    case -1:
                    default:
                        this.status = "Offline";
                }
                this.areaId = rs.getInt(4);
                this.areaName = rs.getString(5);
                this.x_pos = rs.getInt(6);
                this.y_pos = rs.getInt(7);
            }
            Util.safeClose(rs);
            logger.debug("end of querying site's brief recordList");

            alarmStats = new AlarmStats(conn, id);

            logger.debug("start querying site's signal recordList");
            String detailsQuery = String.format(SqlStmts.SITE_DETAILS_SIGNALS, id, BootstrapServlet.getRequiredSignalsForSiteDetails());
            logger.debug("signals details query string is " + detailsQuery);
            rs = stmt.executeQuery(detailsQuery);
            while (rs.next()) {
                recordList.add(new SignalRecord(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
            totalRecords = recordList.size();
            logger.debug("end of querying site's signal recordList");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
