package com.zyt.hisdatastats.monthly;

import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;

/**
 * Created by Administrator on 2015/10/3.
 */
public class MHisDataStats {
    private static final Logger logger = LogManager.getLogger(MHisDataStats.class);

    public static final String REQUIRED_SIGNALS = "957,958,977,980,983,986";  //obsoleted on 2015.12.12
    private HourlyHisDataStats hourlyHisDataStats;
    private DailyHisDataStats dailyHisDataStats;
    private MonthlyHisDataStats monthlyHisDataStats;
    private int curYear;
    private int curMon;

    public MHisDataStats(Connection conn) {
        Calendar calendar = Calendar.getInstance();

        curYear = calendar.get(Calendar.YEAR);
        curMon = calendar.get(Calendar.MONTH); //be aware of that the return value is started from 0

        Calendar forFileName = (Calendar)calendar.clone();
        forFileName.add(Calendar.MONTH, -1);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(BootstrapServlet.getStatsFilesFolder() + "StatisticsDataReport-"
                    + String.format("%1$04d", forFileName.get(Calendar.YEAR))
                    + String.format("%1$02d", forFileName.get(Calendar.MONTH) + 1) + ".xlsx");
        } catch (FileNotFoundException e) {
            logger.error("failed to create the exporting file");
            return;
        }

        SXSSFWorkbook wb = new SXSSFWorkbook(BootstrapServlet.getMaxHisDataExportCacheLine());//new XSSFWorkbook();

        try {
            hourlyHisDataStats = new HourlyHisDataStats(conn, calendar, wb);
        } catch (Exception e) {
            logger.error("failed to export hourly stats");
        }

        try {
            dailyHisDataStats = new DailyHisDataStats(conn, calendar, wb);
        } catch (Exception e) {
            logger.error("failed to export daily stats");
        }

        try {
            monthlyHisDataStats = new MonthlyHisDataStats(conn, calendar, wb);
        } catch (Exception e) {
            logger.error("failed to export monthly stats");
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

    public void export() {
        /*
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(BootstrapServlet.getStatsFilesFolder() + "StatisticsDataReport-"
                    + String.format("%1$04d", curYear) + String.format("%1$02d", curMon) + ".xlsx");
        } catch (FileNotFoundException e) {
            logger.error("failed to create the exporting file");
            return;
        }

        SXSSFWorkbook wb = new SXSSFWorkbook(BootstrapServlet.getMaxHisDataExportCacheLine());//new XSSFWorkbook();

        try {
            hourlyHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export hourly stats");
        }
        try {
            dailyHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export daily stats");
        }
        try {
            monthlyHisDataStats.export(wb);
        } catch (IOException e) {
            logger.error("failed to export monthly stats");
        }

        try {
            wb.write(out);
            out.flush();
        } catch (IOException e) {
            logger.error("failed to export stats");
        } finally {
            Util.safeClose(out, wb);
        }
        */
    }

    public static SXSSFRow createHeader(SXSSFSheet sheet) {
        SXSSFRow row = sheet.createRow(0);
        SXSSFCell cell = row.createCell(0);
        cell.setCellValue("Object");
        cell = row.createCell(1);
        cell.setCellValue("Fan running time, hours");
        cell = row.createCell(2);
        cell.setCellValue("A/C 1  running time, hours");
        cell = row.createCell(3);
        cell.setCellValue("A/C 2 running time, hours");
        cell = row.createCell(4);
        cell.setCellValue("Fan Consumption, kWh");
        cell = row.createCell(5);
        cell.setCellValue("Min indoor temperature");
        cell = row.createCell(6);
        cell.setCellValue("Min outdoor temperature");
        cell = row.createCell(7);
        cell.setCellValue("Max indoor temperature");
        cell = row.createCell(8);
        cell.setCellValue("Max outdoor temperature");
        cell = row.createCell(9);
        cell.setCellValue("Average indoor temperature");
        cell = row.createCell(10);
        cell.setCellValue("Average outdoor temperature");
        cell = row.createCell(11);
        cell.setCellValue("Start Date");
        cell = row.createCell(12);
        cell.setCellValue("End Date");
        return row;
    }

    public static final String KEYSEPERATOR = "___";

    public static String getSiteId(String key) {
        int index = key.indexOf(MHisDataStats.KEYSEPERATOR);
        return key.substring(index + MHisDataStats.KEYSEPERATOR.length());
    }

    public static String getReminder(String key) {
        int index = key.indexOf(MHisDataStats.KEYSEPERATOR);
        return key.substring(0, index);// + MHisDataStats.KEYSEPERATOR.length());
    }
}
