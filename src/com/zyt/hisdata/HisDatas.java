package com.zyt.hisdata;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/28.
 */
public class HisDatas {
    private static final Logger logger = LogManager.getLogger(HisDatas.class);

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<HisDataRecord> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<HisDataRecord> recordList) {
        this.recordList = recordList;
    }

    private int totalRecords;
    private ArrayList<HisDataRecord> recordList = new ArrayList<HisDataRecord>();

    //public static final String REQUIRED_SIGNALS = "957,958,959,969,978,981,984,985,986";

    public HisDatas(Connection conn, int start, int amount, String orderField, String orderDirection,
                    int zoneId, String zoneName, int siteId, String siteName, int signalId, String signalName,
                    int interval, String startTime, String endTime) {
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
            } else if (orderField.equalsIgnoreCase("time")) {
                orderBy = "DataTime";
            } else if (orderField.equalsIgnoreCase("value")) {
                orderBy = "DataVal";
            }
        }
        String orderDir = "desc";
        if (orderDirection != null && orderDirection.equalsIgnoreCase("asc")) {
            orderDir = "asc";
        }
        StringBuilder conditions = new StringBuilder();
        if (siteId != -1) {
            conditions.append("and b.site_id=").append(siteId).append(' ');
        } else if (siteName != null) {
            conditions.append("and b.name='").append(siteName).append("\' ");
        } else if (zoneId != -1) {
            conditions.append("and c.zone_id=").append(zoneId).append(' ');
        } else if (zoneName != null) {
            conditions.append("and c.zone_name='").append(zoneName).append("' ");
        }

        if (signalId != -1) {
            conditions.append("and a.signalNo=").append(signalId).append(' ');
        } else if (signalName != null) {
            conditions.append("and a.signalName='").append(signalName).append("' ");
        }

        if (interval != -1) {
            conditions.append("and a.save_interval=").append(interval).append(' ');
        }

        if (startTime != null) {
            conditions.append("and convert(varchar(10), a.DataTime, 120) >= '").append(startTime).append("'");
        }
        if (endTime != null) {
            conditions.append("and convert(varchar(10), a.DataTime, 120) <= '").append(endTime).append("'");
        }

//        String queryTotalRecordsStmt = String.format(SqlStmts.SELECT_HIS_DATAS_2005_TOTALNUMBER,
//                BootstrapServlet.getRequiredSignalsForHisData(), conditions.toString());
//        logger.debug("history datas total number query stmt: " + queryTotalRecordsStmt);  //changed from alarms to datas on 2015.11.18
//        try {
//            logger.debug("start querying history datas total number");
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(queryTotalRecordsStmt);
//            if (rs.next()) {
//                this.totalRecords = rs.getInt(1);
//            }
//            Util.safeClose(rs, stmt);
//            logger.debug("end of querying history datas total number");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            Util.safeClose(rs, stmt);
//        }
//
        if (amount == -1) {
            amount = BootstrapServlet.getMaxHisRecordsAmount();  //this.totalRecords + 1;  //changed on 2015.11.18
        }
        String queryStmt = String.format(SqlStmts.SELECT_HIS_DATAS_2005, orderBy, orderDir,
                BootstrapServlet.getRequiredSignalsForHisData(), conditions.toString(), start, (start + amount),
                BootstrapServlet.getMaxHisRecordsAmount());
        logger.debug("queryStr is " + queryStmt);
        try {
            stmt = conn.createStatement();
            logger.debug("start querying history datas");
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                totalRecords++;  //modified on 2015.11.18
                if (totalRecords >= start) {
                    recordList.add(new HisDataRecord(rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getString(5),
                            rs.getInt(6), rs.getString(7), rs.getString(8), rs.getString(9)));
                }
                if (totalRecords == (start + amount - 1)) {
                    break;
                }
            }
            while (rs.next()) {
                totalRecords++;
            }

            logger.debug("end of querying history datas");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
