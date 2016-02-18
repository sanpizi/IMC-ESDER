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
class DailyHisDataStats {
    private static final Logger logger = LogManager.getLogger(DailyHisDataStats.class);

    private ArrayList<DailyHisDataStatsRecord> recordList = new ArrayList<DailyHisDataStatsRecord>(1000);
    private ArrayList<DailyHisDataStatsRecord> accumulatingRecordList = new ArrayList<DailyHisDataStatsRecord>(1000);
    private HashMap<String, Float> accumulatingRecordMap = new HashMap<String, Float>(); //SiteId___SignalNo___Day, value
    private HashMap<String, HashMap<Integer, String>> dailyExportData = new HashMap<String, HashMap<Integer, String>>(1000); //[day___siteid, [columnindex, value]]

    private HashMap<Integer, String> siteId2Name = new HashMap<Integer, String>();

    private void setSiteInfo(Integer id, String name) {
        siteId2Name.put(id, name);
    }

    private String getSiteName(Integer id) {
        return siteId2Name.get(id);
    }

    private void putExportValue(String key, int columnIndex, float value) {
        HashMap<Integer, String> dataMap = dailyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            dailyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, String.format("%1$.2f", value));
    }

    private void putExportValue(String key, int columnIndex, String value) {
        HashMap<Integer, String> dataMap = dailyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            dailyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, value);
    }

    private void constructExportData(int ignoredDay) {
        logger.debug("start to construct daily export data");
        for (DailyHisDataStatsRecord record : recordList) {
            String dailyKey  = record.getDay() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
            int columnIndex;
            float dailyValue;
            switch (record.getSignalNo()) {
                case 958:  //column 5, 7, 9
                    columnIndex = 5;
                    dailyValue = record.getMinValue();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    columnIndex = 7;
                    dailyValue = record.getMaxValue();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    columnIndex = 9;
                    dailyValue = record.getAverage();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    break;
                case 957:  //column 6, 8, 10
                    columnIndex = 6;
                    dailyValue = record.getMinValue();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    columnIndex = 8;
                    dailyValue = record.getMaxValue();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    columnIndex = 10;
                    dailyValue = record.getAverage();
                    putExportValue(dailyKey, columnIndex, dailyValue);
                    break;
                default:
                    continue;
            }
        }

        //for accumulating record
        for (DailyHisDataStatsRecord record : accumulatingRecordList) {
            if (getDay(record.getDay()) == ignoredDay) {
                continue;
            }
            String dailyKey = record.getDay() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
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
            putExportValue(dailyKey, columnIndex, getAccumulatingValue(record));
        }

        logger.debug("end of constructing daily export data");
    }

    private String getAccumulatingValue(DailyHisDataStatsRecord record) {
        Float value = accumulatingRecordMap.get(record.getSiteId() + MHisDataStats.KEYSEPERATOR + record.getSignalNo() + MHisDataStats.KEYSEPERATOR + getNextDate(record.getDay()));
        if (value == null) {
            return "-";
        }
        return String.format("%1$.2f", value.floatValue() - record.getAccumulatedValue());
    }

    private int getDay(String day) {
        int index = day.lastIndexOf('-');
        return Integer.parseInt(day.substring(index + 1));
    }

    private String getNextDate(String date) {
        int yearIndex = date.indexOf('-');
        int monthIndex = date.indexOf('-', yearIndex + 1);
        int year = Integer.parseInt(date.substring(0, yearIndex));
        int month = Integer.parseInt(date.substring(yearIndex + 1, monthIndex)) - 1;
        int day = Integer.parseInt(date.substring(monthIndex + 1));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, 0, 0, 0);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-"
                + (month < 9 ? "0" : "") + (month + 1) + "-"
                + (day < 10 ? "0" : "") + day;
    }

    public DailyHisDataStats(Connection conn, Calendar curCalendar, SXSSFWorkbook wb) {
        SXSSFSheet sheet = wb.createSheet("Day");
        MHisDataStats.createHeader(sheet);
        int rowIndex = 1;

        Statement stmt = null;
        ResultSet rs = null;

        Calendar startCalendar = (Calendar)curCalendar.clone();
        startCalendar.add(Calendar.MONTH, -1);

        while (startCalendar.before(curCalendar)) {
            Calendar tmpCurCalendar = (Calendar)startCalendar.clone();
            tmpCurCalendar.add(Calendar.DAY_OF_MONTH, 1);

            String queryStmt = String.format(SqlStmts.HISDATA_STATS_DAILY, "957,958",
                    startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                    tmpCurCalendar.get(Calendar.YEAR), tmpCurCalendar.get(Calendar.MONTH) + 1, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            logger.debug("The query string is " + queryStmt);
            int count = 0;
            try {
                logger.debug("start querying daily history data stats");
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStmt);
                while (rs.next()) {
                    count++;
                    int siteId = rs.getInt(1);
                    recordList.add(new DailyHisDataStatsRecord(siteId, rs.getInt(2), rs.getString(3),
                            rs.getFloat(4), rs.getFloat(5), rs.getInt(6), rs.getFloat(7), -1));
                    setSiteInfo(siteId, rs.getString(8));
                }
                Util.safeClose(rs, stmt);
                logger.debug("end of querying daily history data stats, count=" + count);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Util.safeClose(rs, stmt);
            }

            //query accumulated values begins
            queryStmt = String.format(SqlStmts.HISDATA_STATS_DAILY_ACCUMULATED, "977,980,983,986",
                    startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                    tmpCurCalendar.get(Calendar.YEAR), tmpCurCalendar.get(Calendar.MONTH) + 1, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            logger.debug("The query string is " + queryStmt);
            count = 0;
            try {
                logger.debug("start querying daily history data stats");
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStmt);
                while (rs.next()) {
                    count++;
                    int siteId = rs.getInt(1);
                    int signalNo = rs.getInt(2);
                    String dataTime = rs.getString(3);
                    float value = rs.getFloat(4);
                    accumulatingRecordList.add(new DailyHisDataStatsRecord(siteId, signalNo, dataTime,
                            -1, -1, -1, -1, value));
                    accumulatingRecordMap.put(siteId + MHisDataStats.KEYSEPERATOR + signalNo + MHisDataStats.KEYSEPERATOR + dataTime, value);
                    setSiteInfo(siteId, rs.getString(5));
                }
                Util.safeClose(rs, stmt);
                logger.debug("end of querying daily history data stats, count=" + count);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Util.safeClose(rs, stmt);
            }

            rowIndex = dailyExport(sheet, rowIndex, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            recordList.clear();
            accumulatingRecordList.clear();
            accumulatingRecordMap.clear();
            dailyExportData.clear();

            startCalendar = tmpCurCalendar;
        }
    }

    private int dailyExport(SXSSFSheet sheet, int rowIndex, int ignoredDay) {
        constructExportData(ignoredDay);
        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
        keySet.addAll(dailyExportData.keySet());

        for (String key : keySet) {
            toRow(sheet, rowIndex, key);
            rowIndex++;
        }

        return rowIndex;
    }

//    public void export(SXSSFWorkbook wb) throws IOException {
//        constructExportData();
//        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
//        keySet.addAll(dailyExportData.keySet());
//
//        SXSSFSheet sheet = wb.createSheet("Day");
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
            String value = dailyExportData.get(key).get(i);
            cell.setCellValue(value == null ? "-" : value);
        }
        cell = row.createCell(11);
        cell.setCellValue(MHisDataStats.getReminder(key) + " 00:00");
        cell = row.createCell(12);
        cell.setCellValue(getNextDate(MHisDataStats.getReminder(key)) + " 00:00");
        return row;
    }
}
