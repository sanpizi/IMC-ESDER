package com.zyt.sites;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.alarm.RealTimeAlarms;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/1.
 */
public class Sites {
    private static final Logger logger = LogManager.getLogger(Sites.class);

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<Site> getSiteList() {
        return siteList;
    }

    public void setSiteList(ArrayList<Site> siteList) {
        this.siteList = siteList;
    }

    private int totalRecords;
    private ArrayList<Site> siteList = new ArrayList<Site>();

    public Sites(Connection conn, int startRecord, int recordNum, String orderField, String orderDirection, String status, String[] ids) {
        Statement stmt = null;
        ResultSet rs = null;

        int count = 0;

        String orderBy = "site_id";
        if (orderField != null) {
            if (orderField.equalsIgnoreCase("id")) {
                orderBy = "site_id";
            } else if (orderField.equalsIgnoreCase("name")) {
                orderBy = "name";
            } else if (orderField.equalsIgnoreCase("status")) {
                orderBy = "DataAlarm";
            } else if (orderField.equalsIgnoreCase("areaId")) {
                orderBy = "zone_id";
            } else if (orderField.equalsIgnoreCase("areaName")) {
                orderBy = "zone_name";
            }
        }
        String orderDir = "asc";
        if (orderDirection != null && orderDirection.equalsIgnoreCase("desc")) {
            orderDir = "desc";
        }
        StringBuilder statusCondition = new StringBuilder();
        if (status != null) {
            if (status.equalsIgnoreCase("Normal")) {
                statusCondition.append("and DataAlarm=0");
            } else if (status.equalsIgnoreCase("Offline")) {
                statusCondition.append("and DataAlarm=-1");
            } else if (status.equalsIgnoreCase("Alarm")) {
                statusCondition.append("and DataAlarm=1");
            }
        }

        StringBuilder zoneIds = new StringBuilder();
        for (String id : ids) {
            zoneIds.append(id).append(',');
        }
        String zoneIdConditions = zoneIds.toString();
        zoneIdConditions = zoneIdConditions.substring(0, zoneIdConditions.length() - 1);

        String statusConditionForTotalNumber = "";
        if (status != null) {
            statusConditionForTotalNumber = statusCondition.toString().replaceFirst("and", "where");
        }
        String queryTotalRecordsStmt = String.format(SqlStmts.SITES_WITH_AREAID_SPECIFIED_TOTALNUMBER, statusConditionForTotalNumber, zoneIdConditions);
        logger.debug("sitesTotalRecordsStmt: " + queryTotalRecordsStmt);
        try {
            logger.debug("start querying sites total number");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryTotalRecordsStmt);
            if (rs.next()) {
                this.totalRecords = rs.getInt(1);
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying sites total number");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        if (recordNum == -1) {
            recordNum = this.totalRecords;
        }
        String queryStmt = String.format(SqlStmts.SITES_WITH_AREAID_SPECIFIED, orderBy, orderDir,
                startRecord, (startRecord + recordNum),
                statusCondition.toString(), zoneIdConditions);
        logger.debug("sitesQueryStmt: " + queryStmt);
        try {
            logger.debug("start querying sites details");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                int siteId = rs.getInt(2);
                String siteName = rs.getString(3);
                String siteStatus = "Offline";
                switch (rs.getInt(4)) {
                    case 1:
                        siteStatus = "Alarm";
                        break;
                    case 0:
                        siteStatus = "Normal";
                        break;
                    case -1:
                    default:
                        siteStatus = "Offline";
                }
                int areaId = rs.getInt(5);
                String areaName = rs.getString(6);
                int x_pos = rs.getInt(7);
                int y_pos = rs.getInt(8);
                siteList.add(new Site(siteId, siteName, siteStatus, areaId, areaName, x_pos, y_pos));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying sites details, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public Sites(Connection conn, int startRecord, int recordNum, String orderField, String orderDirection, String status) {
        Statement stmt = null;
        ResultSet rs = null;

        int count = 0;

        String orderBy = "site_id";
        if (orderField != null) {
            if (orderField.equalsIgnoreCase("id")) {
                orderBy = "site_id";
            } else if (orderField.equalsIgnoreCase("name")) {
                orderBy = "name";
            } else if (orderField.equalsIgnoreCase("status")) {
                orderBy = "DataAlarm";
            } else if (orderField.equalsIgnoreCase("areaId")) {
                orderBy = "zone_id";
            } else if (orderField.equalsIgnoreCase("areaName")) {
                orderBy = "zone_name";
            }
        }
        String orderDir = "asc";
        if (orderDirection != null && orderDirection.equalsIgnoreCase("desc")) {
            orderDir = "desc";
        }
        StringBuilder statusCondition = new StringBuilder();
        if (status != null) {
            if (status.equalsIgnoreCase("Normal")) {
                statusCondition.append("and DataAlarm=0");
            } else if (status.equalsIgnoreCase("Offline")) {
                statusCondition.append("and DataAlarm=-1");
            } else if (status.equalsIgnoreCase("Alarm")) {
                statusCondition.append("and DataAlarm=1");
            }
        }

        String queryTotalRecordsStmt = String.format(SqlStmts.SITES_TOTALNUMBER, statusCondition.toString());
        logger.debug("sitesTotalRecordsStmt: " + queryTotalRecordsStmt);
        try {
            logger.debug("start querying sites total number");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryTotalRecordsStmt);
            if (rs.next()) {
                this.totalRecords = rs.getInt(1);
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying sites total number");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        if (recordNum == -1) {
            recordNum = this.totalRecords + 1;
        }
        String queryStmt = String.format(SqlStmts.SITES, orderBy, orderDir,
                startRecord, (startRecord + recordNum),
                statusCondition.toString());
        logger.debug("sitesQueryStmt: " + queryStmt);
        try {
            logger.debug("start querying sites details");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                int siteId = rs.getInt(2);
                String siteName = rs.getString(3);
                String siteStatus = "Offline";
                switch (rs.getInt(4)) {
                    case 1:
                        siteStatus = "Alarm";
                        break;
                    case 0:
                        siteStatus = "Normal";
                        break;
                    case -1:
                    default:
                        siteStatus = "Offline";
                }
                int areaId = rs.getInt(5);
                String areaName = rs.getString(6);
                int x_pos = rs.getInt(7);
                int y_pos = rs.getInt(8);
                siteList.add(new Site(siteId, siteName, siteStatus, areaId, areaName, x_pos, y_pos));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying sites details, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
