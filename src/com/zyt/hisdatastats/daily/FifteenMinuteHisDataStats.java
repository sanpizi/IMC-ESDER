package com.zyt.hisdatastats.daily;

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
import java.util.*;

/**
 * Created by Administrator on 2015/10/3.
 */
class FifteenMinuteHisDataStats {
    private static final Logger logger = LogManager.getLogger(FifteenMinuteHisDataStats.class);

    private ArrayList<MinuteHisDataStatsRecord> recordList = new ArrayList<MinuteHisDataStatsRecord>(1000);
//    private ArrayList<MinuteHisDataStatsRecord> accumulatingRecordList = new ArrayList<MinuteHisDataStatsRecord>(1000);
//    private HashMap<String, Float> accumulatingRecordMap = new HashMap<String, Float>(); //SiteId___SignalNo___Minute, value
    private HashMap<String, HashMap<Integer, HashMap<Integer, String>>> minuteExportData = new HashMap<String, HashMap<Integer, HashMap<Integer, String>>>(); //[minute, [siteid, [columnindex, value]]]
    private HashMap<Integer, String> siteId2Name = new HashMap<Integer, String>();

    private Calendar curCalendar;

    private void setSiteInfo(Integer id, String name) {
        siteId2Name.put(id, name);
    }

    private String getSiteName(Integer id) {
        return siteId2Name.get(id);
    }

    private void putExportValue(String minute, int siteId, int columnIndex, float value) {
        HashMap<Integer, HashMap<Integer, String>> siteDataMap = minuteExportData.get(minute);
        if (siteDataMap == null) {
            siteDataMap = new HashMap<Integer, HashMap<Integer, String>>();
            minuteExportData.put(minute, siteDataMap);
        }
        HashMap<Integer, String> dataMap = siteDataMap.get(siteId);
        if (dataMap == null) {
            dataMap = new HashMap<Integer, String>();
            siteDataMap.put(siteId, dataMap);
        }
        dataMap.put(columnIndex, columnIndex == 3 ? String.format("%1$.0f", value) : String.format("%1$.2f", value));
    }

//    private void putExportValue(String minute, int siteId, int columnIndex, String value) {
//        HashMap<Integer, HashMap<Integer, String>> siteDataMap = minuteExportData.get(minute); //[minute, [siteid, [columnindex, value]]]
//        if (siteDataMap == null) {
//            siteDataMap = new HashMap<Integer, HashMap<Integer, String>>();
//            minuteExportData.put(minute, siteDataMap);
//        }
//        HashMap<Integer, String> dataMap = siteDataMap.get(siteId);
//        if (dataMap == null) {
//            dataMap = new HashMap<Integer, String>();
//            siteDataMap.put(siteId, dataMap);
//        }
//        dataMap.put(columnIndex, value);
//    }

    private void constructExportData() {
        logger.debug("start to construct 15 minutes export data");
        for (MinuteHisDataStatsRecord record : recordList) {
            int columnIndex;
            float minuteValue;
            switch (record.getSignalNo()) {
                case 957:  //column 1
                    columnIndex = 1;
                    minuteValue = record.getAverage();
                    break;
                case 958:  //column 2
                    columnIndex = 2;
                    minuteValue = record.getAverage();
                    break;
                case 959:  //column 3
                    columnIndex = 3;
                    minuteValue = record.getAverage();
                    break;
                case 977:  //column 4
                    columnIndex = 4;
                    minuteValue = record.getMaxValue();
                    break;
                case 980:  //column 5
                    columnIndex = 5;
                    minuteValue = record.getMaxValue();
                    break;
                case 983:  //column 6
                    columnIndex = 6;
                    minuteValue = record.getMaxValue();
                    break;
                case 985:  //column 7
                    columnIndex = 7;
                    minuteValue = record.getMaxValue();
                    break;
                case 986:  //column 8
                    columnIndex = 8;
                    minuteValue = record.getMaxValue();
                    break;
                default:
                    continue;
            }
            putExportValue(record.getMinute(), record.getSiteId(), columnIndex, minuteValue);
        }
//        for (MinuteHisDataStatsRecord record : accumulatingRecordList) {
//            if (getDay(record.getMinute()) == curCalendar.get(Calendar.DAY_OF_MONTH)) {
//                continue;
//            }
//            int columnIndex;
//            String minuteValue;
//            switch (record.getSignalNo()) {
//                case 986:  //column 8
//                    columnIndex = 8;
//                    minuteValue = getAccumulatedValue(record);
//                    break;
//                default:
//                    continue;
//            }
//            putExportValue(record.getMinute(), record.getSiteId(), columnIndex, minuteValue);
//        }
        logger.debug("end of constructing 15 minutes export data");
    }

//    private String getAccumulatedValue(MinuteHisDataStatsRecord record) {
//        Float value = accumulatingRecordMap.get(record.getSiteId() + DHisDataStats.KEYSEPERATOR + record.getSignalNo() + DHisDataStats.KEYSEPERATOR + getNextMinutes(record.getMinute()));
//        if (value == null) {
//            return "-";
//        }
//        return String.format("%1$.2f", value.floatValue() - record.getAccumulatedValue());
//    }

//    private String getNextMinutes(String minute) {
//        int yearIndex = minute.indexOf('-');
//        int monthIndex = minute.indexOf('-', yearIndex + 1);
//        int timeIndex = minute.indexOf(' ');
//        int hourIndex = minute.indexOf(':', timeIndex + 1);
//        int year = Integer.parseInt(minute.substring(0, yearIndex));
//        int month = Integer.parseInt(minute.substring(yearIndex + 1, monthIndex)) - 1;  //0 based
//        int day = Integer.parseInt(minute.substring(monthIndex + 1, timeIndex));
//        int hour = Integer.parseInt(minute.substring(timeIndex + 1, hourIndex));
//        int exactMinute = Integer.parseInt(minute.substring(hourIndex + 1));
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year, month, day, hour, exactMinute, 0);
//        calendar.add(Calendar.MINUTE, 15);
//        year = calendar.get(Calendar.YEAR);
//        month = calendar.get(Calendar.MONTH) + 1;
//        day = calendar.get(Calendar.DAY_OF_MONTH);
//        hour = calendar.get(Calendar.HOUR_OF_DAY);
//        exactMinute = calendar.get(Calendar.MINUTE);
//        return year + "-" + (month < 10 ? "0" : "") + month
//                + "-" + (day < 10 ? "0" : "") + day + " "
//                + (hour < 10 ? "0" : "") + hour + ":"
//                + (exactMinute < 10 ? "0" : "") + exactMinute;
//    }
//
//    private int getDay(String minute) {
//        String date = minute.substring(0, minute.indexOf(' '));
//        int index = date.lastIndexOf('-');
//        return Integer.parseInt(date.substring(index + 1));
//    }

    public FifteenMinuteHisDataStats(Connection conn, Calendar startCalendar) {
        Statement stmt = null;
        ResultSet rs = null;

        Calendar endCalendar = (Calendar)startCalendar.clone();
        endCalendar.add(Calendar.DAY_OF_MONTH, 1);
        this.curCalendar = endCalendar;

        String queryStmt = String.format(SqlStmts.DAILY_HISDATA_STATS_15MINUTE, DHisDataStats.REQUIRED_SIGNALS, //"957,958,959,977,980,983,985",
                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
                endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH) + 1, endCalendar.get(Calendar.DAY_OF_MONTH));
        logger.debug("The query string is " + queryStmt);
        int count = 0;
        try {
            logger.debug("start querying 15 minutes history data stats");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStmt);
            while (rs.next()) {
                count++;
                int siteId = rs.getInt(1);
                recordList.add(new MinuteHisDataStatsRecord(siteId, rs.getInt(2), rs.getString(3),
                        rs.getFloat(4), rs.getFloat(5), rs.getInt(6), rs.getFloat(7), -1));
                setSiteInfo(siteId, rs.getString(8));
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying 15 minutes history data stats, count=" + count);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        //query accumulating values
//        queryStmt = String.format(SqlStmts.DAILY_HISDATA_STATS_15MINUTE_ACCUMULATED, "986",
//                startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.DAY_OF_MONTH),
//                curCalendar.get(Calendar.YEAR), curCalendar.get(Calendar.MONTH) + 1, curCalendar.get(Calendar.DAY_OF_MONTH));
//        logger.debug("The query string is " + queryStmt);
//        count = 0;
//        try {
//            logger.debug("start querying 15 minutes history data stats");
//            stmt = conn.createStatement();
//            rs = stmt.executeQuery(queryStmt);
//            while (rs.next()) {
//                count++;
//                int siteId = rs.getInt(1);
//                int signalNo = rs.getInt(2);
//                String dataTime = rs.getString(3);
//                float value = rs.getFloat(4);
//                accumulatingRecordList.add(new MinuteHisDataStatsRecord(siteId, signalNo, dataTime,
//                        -1, -1, -1, -1, value));
//                accumulatingRecordMap.put(siteId + DHisDataStats.KEYSEPERATOR + signalNo + DHisDataStats.KEYSEPERATOR + dataTime, value);
//                setSiteInfo(siteId, rs.getString(5));
//            }
//            Util.safeClose(rs, stmt);
//            logger.debug("end of querying 15 minutes history data stats, count=" + count);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        } finally {
//            Util.safeClose(rs, stmt);
//        }
    }

    public void export(SXSSFWorkbook wb) throws IOException {
        constructExportData();
        TreeSet<String> keySet = new TreeSet<String>();
        keySet.addAll(minuteExportData.keySet());

        ArrayList<Integer> siteList = new ArrayList<Integer>();
        for (String minute : keySet) {
            Set<Integer> idSet = minuteExportData.get(minute).keySet();
            for (Integer id : idSet) {
                if (!siteList.contains(id)) {
                    siteList.add(id);
                }
            }
        }
        Collections.sort(siteList);

        SXSSFSheet sheet = wb.createSheet("15Minute");
        DHisDataStats.createHeader(sheet, siteList, siteId2Name);
        int rowIndex = 2;
        for (String minute : keySet) {
            toRow(sheet, rowIndex, minute, siteList);
            rowIndex++;
            if (rowIndex >= 65535) {
                break;
            }
        }
    }

    private SXSSFRow toRow(SXSSFSheet sheet, int rowIndex, String minute, ArrayList<Integer> siteList) {
        SXSSFRow row = sheet.createRow(rowIndex);
        SXSSFCell cell = row.createCell(0);
        cell.setCellValue(minute);
        HashMap<Integer, HashMap<Integer, String>> siteDataMap = minuteExportData.get(minute);
        for (Integer siteId : siteList) {
            HashMap<Integer, String> dataMap = siteDataMap.get(siteId);
            int index = siteList.indexOf(siteId) * 8;
            for (int i = 1; i < 9; i++) {
                cell = row.createCell(i + index);
                String value = dataMap == null ? null : dataMap.get(i);
                cell.setCellValue(value == null ? "-" : value);
            }
        }
        return row;
    }
}
