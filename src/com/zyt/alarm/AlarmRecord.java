package com.zyt.alarm;

/**
 * Created by Administrator on 2015/8/20.
 */
public class AlarmRecord {
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

    private int areaId;
    private String areaName;
    private int siteId;
    private String siteName;
    private int signalId;
    private String signalName;
    private String severity;
    private String startTime;

    public AlarmRecord(int zoneId, String zoneName, int siteId, String siteName, int signalId, String signalName, String severity, String startTime) {
        this.areaId = zoneId;
        this.areaName = zoneName;
        this.siteId = siteId;
        this.siteName = siteName;
        this.signalId = signalId;
        this.signalName = signalName;
        this.severity = severity;
        this.startTime = startTime;
    }
}
