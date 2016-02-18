package com.zyt.alarm;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/20.
 */
public class HisAlarms {
    private static final Logger logger = LogManager.getLogger(HisAlarms.class);

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<HisAlarmRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<HisAlarmRecord> recordList) {
        this.recordList = recordList;
    }

    private int totalRecords;
    private ArrayList<HisAlarmRecord> recordList = new ArrayList<HisAlarmRecord>();

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
     * @param startTime
     * @param endTime
     */
    public HisAlarms(Connection conn, int start, int amount, String orderField, String orderDirection,
                     String severity, int zoneId, String zoneName, int siteId, String siteName, int signalId,
                     String signalName, String startTime, String endTime) {
        Statement stmt = null;
        ResultSet rs = null;

        //prepare conditions
        String orderBy = "tmRestored";
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
                orderBy = "tmOccured";
            } else if (orderField.equalsIgnoreCase("endTime")) {
                orderBy = "tmRestored";
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
            conditions.append("and a.typeId=").append(signalId).append(' ');
        } else if (signalName != null) {
            conditions.append("and b.signalName='").append(signalName).append("' ");
        }

        if (startTime != null) {
            conditions.append("and convert(varchar(10), a.tmOccured, 120) >= '").append(startTime).append("'");
        }
        if (endTime != null) {
            conditions.append("and convert(varchar(10), a.tmOccured, 120) <= '").append(endTime).append("'");
        }

//        String queryTotalRecordsStmt = String.format(SqlStmts.SELECT_HIS_ALARMS_2005_TOTALNUMBER,
//                BootstrapServlet.getRequiredTypeIdsForAlarm(), conditions.toString());
//        logger.debug("history alarms total number query stmt: " + queryTotalRecordsStmt);
//        try {
//            logger.debug("start querying history alarms total number");
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(queryTotalRecordsStmt);
//            if (rs.next()) {
//                this.totalRecords = rs.getInt(1);
//            }
//            Util.safeClose(rs, stmt);
//            logger.debug("end of querying history alarms total number");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            Util.safeClose(rs, stmt);
//        }
//
        if (amount == -1) {
            amount = BootstrapServlet.getMaxHisRecordsAmount(); //this.totalRecords + 1;  //changed on 2015.11.18
        }
        String queryStmt = String.format(SqlStmts.SELECT_HIS_ALARMS_2005, orderBy, orderDir,
                BootstrapServlet.getRequiredTypeIdsForAlarm(), conditions.toString(), start, (start + amount),
                BootstrapServlet.getMaxHisRecordsAmount());
        logger.debug("history alarms query stmt: " + queryStmt);
        try {
            stmt = conn.createStatement();
            logger.debug("start querying history alarms");
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                totalRecords++;  //modified on 2015.11.18
                if (totalRecords >= start) {
                    recordList.add(new HisAlarmRecord(rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5),
                            rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9), rs.getString(10)));
                }
                if (totalRecords == (start + amount - 1)) {
                    break;
                }
            }
            while (rs.next()) {
                totalRecords++;
            }
            logger.debug("end of querying history alarms");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public void export(OutputStream out) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("History alarms");

        HisAlarmRecord.createHeader(sheet);
        int rowIndex = 1;
        for (HisAlarmRecord record : recordList) {
            record.toRow(sheet, rowIndex);
            rowIndex++;
            if (rowIndex >= 65535) {
                break;
            }
        }

        wb.write(out);
        out.flush();
        wb.close();
    }
}
