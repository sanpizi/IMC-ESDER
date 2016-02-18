package com.zyt.alarm;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

/**
 * Created by Administrator on 2015/8/20.
 */
public class HisAlarmRecord {
    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public int getSignalId() {
        return signalId;
    }

    public void setSignalId(int signalId) {
        this.signalId = signalId;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    private int areaId;
    private String areaName;
    private int siteId;
    private String siteName;
    private int signalId;
    private String signalName;
    private String severity;
    private String startTime;
    private String endTime;

    /**
     * @param zoneName
     * @param siteName
     * @param signalName
     * @param severity
     * @param startTime
     * @param endTime
     */
    public HisAlarmRecord(int zoneId, String zoneName, int siteId, String siteName, int signalId, String signalName,
                          String severity,String startTime, String endTime) {
        this.areaId = zoneId;
        this.areaName = zoneName;
        this.siteId = siteId;
        this.siteName = siteName;
        this.signalId = signalId;
        this.signalName = signalName;
        this.severity = severity;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static XSSFRow createHeader(XSSFSheet sheet) {
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue("Zone Name");
        cell = row.createCell(1);
        cell.setCellValue("Site Name");
        cell = row.createCell(2);
        cell.setCellValue("Signal Name");
        cell = row.createCell(3);
        cell.setCellValue("Severity");
        cell = row.createCell(4);
        cell.setCellValue("Start Time");
        cell = row.createCell(5);
        cell.setCellValue("End Time");
        return row;
    }

    public XSSFRow toRow(XSSFSheet sheet, int rowIndex) {
        XSSFRow row = sheet.createRow(rowIndex);
        XSSFCell cell = row.createCell(0);
        cell.setCellValue(areaName);
        cell = row.createCell(1);
        cell.setCellValue(siteName);
        cell = row.createCell(2);
        cell.setCellValue(signalName);
        cell = row.createCell(3);
        cell.setCellValue(severity);
        cell = row.createCell(4);
        cell.setCellValue(startTime);
        cell = row.createCell(5);
        cell.setCellValue(endTime);
        return row;
    }
}
