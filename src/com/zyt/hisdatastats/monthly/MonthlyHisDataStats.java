package com.zyt.hisdatastats.monthly;

import com.zyt.SqlStmts;
import com.zyt.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * Created by Administrator on 2015/10/3.
 */
class MonthlyHisDataStats {
    private static final Logger logger = LogManager.getLogger(MonthlyHisDataStats.class);

    private ArrayList<MonthlyHisDataStatsRecord> recordList = new ArrayList<MonthlyHisDataStatsRecord>(1000);
    private ArrayList<MonthlyHisDataStatsRecord> accumulatingRecordList = new ArrayList<MonthlyHisDataStatsRecord>(1000);
    private HashMap<String, Float> accumulatingRecordMap = new HashMap<String, Float>(); //SiteId___SignalNo___month, value
    private HashMap<String, HashMap<Integer, String>> monthlyExportData = new HashMap<String, HashMap<Integer, String>>(1000); //[month___siteid, [columnindex, value]]

    private HashMap<Integer, String> siteId2Name = new HashMap<Integer, String>();

    private Calendar curCalendar;

    private void setSiteInfo(Integer id, String name) {
        siteId2Name.put(id, name);
    }

    private String getSiteName(Integer id) {
        return siteId2Name.get(id);
    }

    private void putExportValue(String key, int columnIndex, float value) {
        HashMap<Integer, String> dataMap = monthlyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            monthlyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, String.format("%1$.2f", value));
    }

    private void putExportValue(String key, int columnIndex, String value) {
        HashMap<Integer, String> dataMap = monthlyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            monthlyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, value);
    }

    private void constructExportData(int ignoredMonth) {
        logger.debug("start to construct monthly export data");
        for (MonthlyHisDataStatsRecord record : recordList) {
            String monthlyKey = record.getMonth() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
            int columnIndex;
            float monthlyValue;
            switch (record.getSignalNo()) {
                case 958:  //column 5, 7, 9
                    columnIndex = 5;
                    monthlyValue = record.getMinValue();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    columnIndex = 7;
                    monthlyValue = record.getMaxValue();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    columnIndex = 9;
                    monthlyValue = record.getAverage();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    break;
                case 957:  //column 6, 8, 10
                    columnIndex = 6;
                    monthlyValue = record.getMinValue();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    columnIndex = 8;
                    monthlyValue = record.getMaxValue();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    columnIndex = 10;
                    monthlyValue = record.getAverage();
                    putExportValue(monthlyKey, columnIndex, monthlyValue);
                    break;
                default:
                    continue;
            }
        }

        //for accumulating record
        for (MonthlyHisDataStatsRecord record : accumulatingRecordList) {
            if (getMonth(record.getMonth()) == ignoredMonth) {
                continue;
            }
            String monthlyKey = record.getMonth() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
            int columnIndex;
            switch (record.getSignalNo()) {
                case 977:  //column 1
                    columnIndex = 1;
                    break;
                case 980:  //column 2
                    columnIndex = 2;
                    break;
                case 983:  //column 3
                    columnIndex = 3;
                    break;
                case 986:  //column 4
                    columnIndex = 4;
                    break;
                default:
                    continue;
            }
            putExportValue(monthlyKey, columnIndex, getAccumulatingValue(record));
        }

        logger.debug("end of constructing monthly export data");
    }

    private String getAccumulatingValue(MonthlyHisDataStatsRecord record) {
        Float value = accumulatingRecordMap.get(record.getSiteId() + MHisDataStats.KEYSEPERATOR + record.getSignalNo() + MHisDataStats.KEYSEPERATOR + getNextMonth(record.getMonth()));
        if (value == null) {
            return "-";
        }
        return String.format("%1$.2f", value.floatValue() - record.getAccumulatedValue());
    }

    private String getNextMonth(String month) {
        int yearIndex = month.indexOf('-');
        int year = Integer.parseInt(month.substring(0, yearIndex));
        int exactMonth = Integer.parseInt(month.substring(yearIndex + 1)) - 1;//0 based
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, exactMonth, 1, 0, 0, 0);
        calendar.add(Calendar.MONTH, 1);
        exactMonth = calendar.get(Calendar.MONTH) + 1;
        return calendar.get(Calendar.YEAR) + "-" + (exactMonth < 10 ? "0" : "") + exactMonth;
    }

    private int getMonth(String month) {
        int index = month.indexOf('-');
        return Integer.parseInt(month.substring(index + 1));
    }

    private String getYear(String key) {
        String month = MHisDataStats.getReminder(key);
        int index = month.indexOf('-');
        return month.substring(0, index);
    }

    private int getExactMonth(String key) {
        String month = MHisDataStats.getReminder(key);
        int index = month.indexOf('-');
        return Integer.parseInt(month.substring(index + 1));
    }

    public MonthlyHisDataStats(Connection conn, Calendar curCalendar, SXSSFWorkbook wb) {
        this.curCalendar = curCalendar;

        SXSSFSheet sheet = wb.createSheet("Month");
        MHisDataStats.createHeader(sheet);
        int rowIndex = 1;

        Statement stmt = null;
        ResultSet rs = null;

        Calendar startCalendar = (Calendar)curCalendar.clone();
        startCalendar.add(Calendar.MONTH, -1);

        String queryStmt = String.format(SqlStmts.HISDATA_STATS_MONTHLY, "957,958",
                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1,
                curCalendar.get(Calendar.YEAR), curCalendar.get(Calendar.MONTH) + 1);
        logger.debug("The query string is " + queryStmt);
        int count = 0;
        try {
            logger.debug("start querying monthly history data stats");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                int siteId = rs.getInt(1);
                recordList.add(new MonthlyHisDataStatsRecord(siteId, rs.getInt(2), rs.getString(3),
                        rs.getFloat(4), rs.getFloat(5), rs.getInt(6), rs.getFloat(7), -1));
                setSiteInfo(siteId, rs.getString(8));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying monthly history data stats, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        //query accumulated values begins
        queryStmt = String.format(SqlStmts.HISDATA_STATS_MONTHLY_ACCUMULATED, "977,980,983,986",
                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1,
                curCalendar.get(Calendar.YEAR), curCalendar.get(Calendar.MONTH) + 1);
        logger.debug("The query string is " + queryStmt);
        HashMap<Integer, HashMap<String, Float>> tmpMap = new HashMap<Integer, HashMap<String, Float>>(); //<SiteId, <HourString, DataVal>>
        count = 0;
        try {
            logger.debug("start querying monthly history data stats");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                int siteId = rs.getInt(1);
                int signalNo = rs.getInt(2);
                String dataTime = rs.getString(3);
                float value = rs.getFloat(4);
                accumulatingRecordList.add(new MonthlyHisDataStatsRecord(siteId, signalNo, dataTime,
                        -1, -1, -1, -1, value));
                accumulatingRecordMap.put(siteId + MHisDataStats.KEYSEPERATOR + signalNo + MHisDataStats.KEYSEPERATOR + dataTime, value);
                setSiteInfo(siteId, rs.getString(5));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying monthly history data stats, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        constructExportData(curCalendar.get(Calendar.MONTH) + 1); //0 based
        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
        keySet.addAll(monthlyExportData.keySet());

        for (String key : keySet) {
            toRow(sheet, rowIndex, key);
            rowIndex++;
        }
    }

//    public void export(SXSSFWorkbook wb) throws IOException {
//        constructExportData();
//        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
//        keySet.addAll(monthlyExportData.keySet());
//
//        SXSSFSheet sheet = wb.createSheet("Month");
//        MHisDataStats.createHeader(sheet);
//        int rowIndex = 1;
//        for (String key : keySet) {
//            toRow(sheet, rowIndex, key);
//            rowIndex++;
//            if (rowIndex >= 65535) {
//                break;
//            }
//        }
//    }

    private SXSSFRow toRow(SXSSFSheet sheet, int rowIndex, String key) {
        SXSSFRow row = sheet.createRow(rowIndex);
        SXSSFCell cell = row.createCell(0);
        String siteId = MHisDataStats.getSiteId(key);
        String siteName = getSiteName(Integer.parseInt(siteId));
        cell.setCellValue(siteName == null ? ("Site_" + siteId) : siteName);
        for (int i = 1; i < 11; i++) {
            cell = row.createCell(i);
            String value = monthlyExportData.get(key).get(i);
            cell.setCellValue(value == null ? "-" : value);
        }
        cell = row.createCell(11);
        cell.setCellValue(MHisDataStats.getReminder(key) + "-01");
        cell = row.createCell(12);
        cell.setCellValue(curCalendar.get(Calendar.YEAR) + "-" + String.format("%1$02d", (curCalendar.get(Calendar.MONTH) + 1)) + "-01");
        return row;
    }
}
