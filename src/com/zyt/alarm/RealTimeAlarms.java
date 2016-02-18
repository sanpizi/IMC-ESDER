package com.zyt.alarm;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/20.
 */
public class RealTimeAlarms {
    private static final Logger logger = LogManager.getLogger(RealTimeAlarms.class);

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<AlarmRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<AlarmRecord> recordList) {
        this.recordList = recordList;
    }

    private int totalRecords;
    private ArrayList<AlarmRecord> recordList = new ArrayList<AlarmRecord>();

//    public static final String REQUIRED_TYPEIDS = "0,6,8,11,14,15,19";

    /**
     * @param conn
     * @param start
     * @param amount
     * @param orderField
     * @param orderDirection
     * @param severity
     * @param zoneName
     * @param siteName
     * @param signalName
     */
    public RealTimeAlarms(Connection conn, int start, int amount, String orderField, String orderDirection,
                          String severity, int zoneId, String zoneName, int siteId, String siteName, int signalId,
                          String signalName) {
        Statement stmt = null;
        ResultSet rs = null;

        //prepare conditions
        String orderBy = "DataTime";
        if (orderField != null) {
            if (orderField.equalsIgnoreCase("areaName")) {
                orderBy = "zone_name";
            } else if (orderField.equalsIgnoreCase("siteName")) {
                orderBy = "name";
            } else if (orderField.equalsIgnoreCase("signalName")) {
                orderBy = "signalName";
            } else if (orderField.equalsIgnoreCase("severity")) {
                orderBy = "levelFlag";
            } else if (orderField.equalsIgnoreCase("startTime")) {
                orderBy = "DataTime";
            }
        }
        String orderDir = "desc";
        if (orderDirection != null && orderDirection.equalsIgnoreCase("asc")) {
            orderDir = "asc";
        }
        StringBuilder conditions = new StringBuilder();
        if (severity != null) {
            if (severity.equalsIgnoreCase("Critical")) {
                conditions.append("and b.levelFlag = 4 ");
            } else if (severity.equalsIgnoreCase("Major")) {
                conditions.append("and b.levelFlag = 3 ");
            } else if (severity.equalsIgnoreCase("Minor")) {
                conditions.append("and b.levelFlag = 2 ");
            } else if (severity.equalsIgnoreCase("Warning")) {
                conditions.append("and b.levelFlag = 1 ");
            }
        }
        if (siteId != -1) {
            conditions.append("and c.site_id=").append(siteId).append(' ');
        } else if (siteName != null) {
            conditions.append("and c.name='").append(siteName).append("\' ");
        } else if (zoneId != -1) {
            conditions.append("and d.zone_id=").append(zoneId).append(' ');
        } else if (zoneName != null) {
            conditions.append("and d.zone_name='").append(zoneName).append("' ");
        }

        if (signalId != -1) {
            conditions.append("and b.typeId=").append(signalId).append(' ');
        } else if (signalName != null) {
            conditions.append("and a.SignalName='").append(signalName).append("' ");
        }

        String queryTotalRecordsStmt = String.format(SqlStmts.SELECT_REALTIME_ALARMS_2005_TOTALNUMBER,
                BootstrapServlet.getRequiredTypeIdsForAlarm(), conditions.toString());
        logger.debug("realtime alarms total number query stmt: " + queryTotalRecordsStmt);
        try {
            logger.debug("start querying realtime alarms total number");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryTotalRecordsStmt);
            if (rs.next()) {
                this.totalRecords = rs.getInt(1);
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying realtime alarms total number");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        if (amount == -1) {
            amount = this.totalRecords + 1;
        }
        String queryStmt = String.format(SqlStmts.SELECT_REALTIME_ALARMS_2005, orderBy, orderDir,
                BootstrapServlet.getRequiredTypeIdsForAlarm(), conditions.toString(), start, (start + amount));
        logger.debug("realtime alarms query stmt: " + queryStmt);
        try {
            stmt = conn.createStatement();
            logger.debug("start querying realtime alarms");
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                recordList.add(new AlarmRecord(rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5),
                        rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9)));
            }
            logger.debug("end of querying realtime alarms");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
