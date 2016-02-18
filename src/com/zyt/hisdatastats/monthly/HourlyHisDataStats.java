package com.zyt.hisdatastats.monthly;

import com.zyt.SqlStmts;
import com.zyt.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
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
class HourlyHisDataStats {
    private static final Logger logger = LogManager.getLogger(HourlyHisDataStats.class);

    private ArrayList<HourlyHisDataStatsRecord> recordList = new ArrayList<HourlyHisDataStatsRecord>(1000);
    private ArrayList<HourlyHisDataStatsRecord> accumulatingRecordList = new ArrayList<HourlyHisDataStatsRecord>(1000);
    private HashMap<String, Float> accumulatingRecordMap = new HashMap<String, Float>(); //SiteId___SignalNo___Hour, value
    private HashMap<String, HashMap<Integer, String>> hourlyExportData = new HashMap<String, HashMap<Integer, String>>(1000); //[hour___siteid, [columnindex, value]]

    private HashMap<Integer, String> siteId2Name = new HashMap<Integer, String>();

    private void setSiteInfo(Integer id, String name) {
        siteId2Name.put(id, name);
    }

    private String getSiteName(Integer id) {
        return siteId2Name.get(id);
    }

    private void putExportValue(String key, int columnIndex, float value) {
        HashMap<Integer, String> dataMap = hourlyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            hourlyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, String.format("%1$.2f", value));
    }

    private void putExportValue(String key, int columnIndex, String value) {
        HashMap<Integer, String> dataMap = hourlyExportData.get(key);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            hourlyExportData.put(key, dataMap);
        }
        dataMap.put(columnIndex, value);
    }

    private void constructExportData(int ignoredDay) {
        logger.debug("start to construct hourly export data");
        for (HourlyHisDataStatsRecord record : recordList) {
            String hourlyKey = record.getHour() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
            int columnIndex;
            float hourlyValue;
            switch (record.getSignalNo()) {
                case 958:  //column 5, 7, 9
                    columnIndex = 5;
                    hourlyValue = record.getMinValue();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    columnIndex = 7;
                    hourlyValue = record.getMaxValue();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    columnIndex = 9;
                    hourlyValue = record.getAverage();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    break;
                case 957:  //column 6, 8, 10
                    columnIndex = 6;
                    hourlyValue = record.getMinValue();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    columnIndex = 8;
                    hourlyValue = record.getMaxValue();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    columnIndex = 10;
                    hourlyValue = record.getAverage();
                    putExportValue(hourlyKey, columnIndex, hourlyValue);
                    break;
                default:
                    continue;
            }
        }

        //for accumulating record
        for (HourlyHisDataStatsRecord record : accumulatingRecordList) {
            if (getDay(record.getHour()) == ignoredDay) {
                continue;
            }
            String hourlyKey = record.getHour() + MHisDataStats.KEYSEPERATOR + record.getSiteId();
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
            putExportValue(hourlyKey, columnIndex, getAccumulatingValue(record));
        }

        logger.debug("end of constructing hourly export data");
    }

    private String getAccumulatingValue(HourlyHisDataStatsRecord record) {
        Float value = accumulatingRecordMap.get(record.getSiteId() + MHisDataStats.KEYSEPERATOR + record.getSignalNo() + MHisDataStats.KEYSEPERATOR + getNextHour(record.getHour()));
        if (value == null) {
            return "-";
        }
        return String.format("%1$.2f", value.floatValue() - record.getAccumulatedValue());
    }

    private String getDate(String key) {
        String hour = MHisDataStats.getReminder(key);
        int index = hour.indexOf(' ');
        return hour.substring(0, index);
    }

    private int getDay(String hour) {
        int index = hour.lastIndexOf('-');
        int index2 = hour.indexOf(' ');
        return Integer.parseInt(hour.substring(index + 1, index2));
    }

    private String getNextHour(String hour) {
        int index = hour.indexOf(' ');
        String date = hour.substring(0, index);
        int yearIndex = date.indexOf('-');
        int monthIndex = date.indexOf('-', yearIndex + 1);
        int year = Integer.parseInt(date.substring(0, yearIndex));
        int month = Integer.parseInt(date.substring(yearIndex + 1, monthIndex)) - 1;  //0 based
        int day = Integer.parseInt(date.substring(monthIndex + 1));
        int exactHour = Integer.parseInt(hour.substring(index + 1));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day, exactHour, 0, 0);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        exactHour = calendar.get(Calendar.HOUR_OF_DAY);
        return year + "-"
                + (month < 9 ? "0" : "") + (month + 1) + "-"
                + (day < 10 ? "0" : "") + day + " "
                + (exactHour < 10 ? "0" : "") + exactHour;
    }

    private String getNextDate(String key) {
        String hour = MHisDataStats.getReminder(key);
        int index = hour.indexOf(' ');
        String date = hour.substring(0, index);
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

    private int getExactHour(String key) {
        String hour = MHisDataStats.getReminder(key);
        int index = hour.indexOf(' ');
        return Integer.parseInt(hour.substring(index + 1));
    }

    public HourlyHisDataStats(Connection conn, Calendar curCalendar, SXSSFWorkbook wb) throws IOException {  //12 -> curCalendar.month=11
        SXSSFSheet sheet = wb.createSheet("Hour");
        MHisDataStats.createHeader(sheet);
        int rowIndex = 1;

        Statement stmt = null;
        ResultSet rs = null;

        Calendar startCalendar = (Calendar)curCalendar.clone();
        startCalendar.add(Calendar.MONTH, -1);

        while (startCalendar.before(curCalendar)) {
            Calendar tmpCurCalendar = (Calendar)startCalendar.clone();
            tmpCurCalendar.add(Calendar.DAY_OF_MONTH, 1);

            String queryStmt = String.format(SqlStmts.HISDATA_STATS_HOURLY, "957,958",
                    startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                    tmpCurCalendar.get(Calendar.YEAR), tmpCurCalendar.get(Calendar.MONTH) + 1, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            logger.debug("The query string is " + queryStmt);
            int count = 0;
            try {
                logger.debug("start querying hourly history data stats");
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStmt);
                while (rs.next()) {
                    count++;
                    int siteId = rs.getInt(1);
                    recordList.add(new HourlyHisDataStatsRecord(siteId, rs.getInt(2), rs.getString(3),
                            rs.getFloat(4), rs.getFloat(5), rs.getInt(6), rs.getFloat(7), -1));
                    setSiteInfo(siteId, rs.getString(8));
                }
                Util.safeClose(rs, stmt);
                logger.debug("end of querying hourly history data stats, count=" + count);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Util.safeClose(rs, stmt);
            }

            //query accumulated values begins
            queryStmt = String.format(SqlStmts.HISDATA_STATS_HOURLY_ACCUMULATED, "977,980,983,986",
                    startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                    tmpCurCalendar.get(Calendar.YEAR), tmpCurCalendar.get(Calendar.MONTH) + 1, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            logger.debug("The query string is " + queryStmt);
            HashMap<Integer, HashMap<String, Float>> tmpMap = new HashMap<Integer, HashMap<String, Float>>(); //<SiteId, <HourString, DataVal>>
            count = 0;
            try {
                logger.debug("start querying hourly history data stats");
                stmt = conn.createStatement();
                rs = stmt.executeQuery(queryStmt);
                while (rs.next()) {
                    count++;
                    int siteId = rs.getInt(1);
                    int signalNo = rs.getInt(2);
                    String dataTime = rs.getString(3);
                    float value = rs.getFloat(4);
                    accumulatingRecordList.add(new HourlyHisDataStatsRecord(siteId, signalNo, dataTime,
                            -1, -1, -1, -1, value));
                    accumulatingRecordMap.put(siteId + MHisDataStats.KEYSEPERATOR + signalNo + MHisDataStats.KEYSEPERATOR + dataTime, value);
                    setSiteInfo(siteId, rs.getString(5));
                }
                Util.safeClose(rs, stmt);
                logger.debug("end of querying hourly history data stats, count=" + count);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Util.safeClose(rs, stmt);
            }

            rowIndex = dailyExport(sheet, rowIndex, tmpCurCalendar.get(Calendar.DAY_OF_MONTH));
            recordList.clear();
            accumulatingRecordList.clear();
            accumulatingRecordMap.clear();
            hourlyExportData.clear();

            startCalendar = tmpCurCalendar;
        }
    }

    private int dailyExport(SXSSFSheet sheet, int rowIndex, int ignoredDay) {
        constructExportData(ignoredDay);
        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
        keySet.addAll(hourlyExportData.keySet());

        for (String key : keySet) {
            toRow(sheet, rowIndex, key);
            rowIndex++;
        }

        return rowIndex;
    }

//    public void export(SXSSFWorkbook wb) throws IOException {
//        constructExportData();
//        TreeSet<String> keySet = new TreeSet<String>();//new SiteIdComparator());
//        keySet.addAll(hourlyExportData.keySet());
//
//        SXSSFSheet sheet = wb.createSheet("Hour");
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
            String value = hourlyExportData.get(key).get(i);
            cell.setCellValue(value == null ? "-" : value);
        }
        cell = row.createCell(11);
        cell.setCellValue(MHisDataStats.getReminder(key) + ":00");
        cell = row.createCell(12);
        if (getExactHour(key) != 23) {
            cell.setCellValue(getDate(key) + " " + String.format("%1$02d", (getExactHour(key) + 1)) + ":00");
        } else {
            cell.setCellValue(getNextDate(key) + " 00:00");
        }
        return row;
    }
}
