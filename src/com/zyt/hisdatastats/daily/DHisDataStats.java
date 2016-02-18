package com.zyt.hisdatastats.daily;

import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/10/3.
 */
public class DHisDataStats {
    private static final Logger logger = LogManager.getLogger(DHisDataStats.class);

    public static final String REQUIRED_SIGNALS = "957,958,959,977,980,983,985,986"; //obsoluted 2015.12.12  //resotred on 2015.12.23

    public static final String KEYSEPERATOR = "___";

    private OneMinuteHisDataStats oneMinuteHisDataStats;
    private FifteenMinuteHisDataStats fifteenMinuteHisDataStats;
    private ThirtyMinuteHisDataStats thirtyMinuteHisDataStats;
    private SixtyMinuteHisDataStats sixtyMinuteHisDataStats;

    private int curYear;
    private int curMon;
    private int curDay;

    public DHisDataStats(Connection conn) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        startCalendar.set(Calendar.SECOND, 0);
        startCalendar.set(Calendar.MILLISECOND, 0);

        startCalendar.add(Calendar.DAY_OF_MONTH, -1);
        curYear = startCalendar.get(Calendar.YEAR);
        curMon = startCalendar.get(Calendar.MONTH) + 1;
        curDay = startCalendar.get(Calendar.DAY_OF_MONTH);

        oneMinuteHisDataStats = new OneMinuteHisDataStats(conn, startCalendar);
        fifteenMinuteHisDataStats = new FifteenMinuteHisDataStats(conn, startCalendar);
        thirtyMinuteHisDataStats = new ThirtyMinuteHisDataStats(conn, startCalendar);
        sixtyMinuteHisDataStats = new SixtyMinuteHisDataStats(conn, startCalendar);
    }

    public void export() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(BootstrapServlet.getStatsFilesFolder() + "HisDataDaily-"
                    + String.format("%1$04d", curYear) + String.format("%1$02d", curMon)
                    + String.format("%1$02d", curDay) + ".xlsx");
        } catch (FileNotFoundException e) {
            logger.error("failed to create the exporting file " + e);
            return;
        }

        SXSSFWorkbook wb = new SXSSFWorkbook(BootstrapServlet.getMaxHisDataExportCacheLine());

        try {
            oneMinuteHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export 5 minute stats");
        }
        try {
            fifteenMinuteHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export 15 minute stats");
        }
        try {
            thirtyMinuteHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export 30 minute stats");
        }
        try {
            sixtyMinuteHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export 60 minute stats");
        }

        try {
            wb.write(out);
            out.flush();
        } catch (IOException e) {
            logger.error("failed to export stats");
        } finally {
            Util.safeClose(out, wb);
        }
    }

    public static SXSSFRow[] createHeader(SXSSFSheet sheet, ArrayList<Integer> siteList, HashMap<Integer, String> siteId2Name) {
        SXSSFRow row = sheet.createRow(0);
        SXSSFRow row2 = sheet.createRow(1);
        SXSSFCell cell = row.createCell(0);
        cell.setCellValue("Timestamp");
        cell = row2.createCell(0);
        cell.setCellValue("");
        //ellRangeAddress(起始行号，终止行号， 起始列号，终止列号）
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));
        for (int i = 0; i < siteList.size(); i++) {
            int index = 8 * i;
            cell = row.createCell(1 + index);
            int siteId = siteList.get(i);
            String siteName = siteId2Name.get(siteId);
            cell.setCellValue(siteName == null ? ("Site_" + siteId) : siteName);
            for (int j = 2; j < 9; j++) {
                cell = row.createCell(j + index);
                cell.setCellValue("");
            }
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 1 + index, 8 + index));
            cell = row2.createCell(1 + index);
            cell.setCellValue("Outdoor Temperature");
            cell = row2.createCell(2 + index);
            cell.setCellValue("Indoor Temperature");
            cell = row2.createCell(3 + index);
            cell.setCellValue("Indoor Humidity, %");
            cell = row2.createCell(4 + index);
            cell.setCellValue("Fan Running Time, h");
            cell = row2.createCell(5 + index);
            cell.setCellValue("A/C 1 Running Time, h");
            cell = row2.createCell(6 + index);
            cell.setCellValue("A/C 2 Running Time, h");
            cell = row2.createCell(7 + index);
            cell.setCellValue("Fan Speed, %");
            cell = row2.createCell(8 + index);
            cell.setCellValue("Fan Consumption, kWh");
        }
        return new SXSSFRow[]{row, row2};
//        return new XSSFRow[]{};
    }
}
