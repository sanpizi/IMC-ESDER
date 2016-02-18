package com.zyt.sync.hisalarm;

/**
 * Created by Administrator on 2015/10/4.
 */
class HisAlarmSyncRecord {
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

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getSignalNo() {
        return signalNo;
    }

    public void setSignalNo(int signalNo) {
        this.signalNo = signalNo;
    }

    public String getSignalName() {
        return signalName;
    }

    public void setSignalName(String signalName) {
        this.signalName = signalName;
    }

    public int getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(int alarmLevel) {
        this.alarmLevel = alarmLevel;
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

    private int siteId;
    private String siteName;
    private int deviceId;
    private String deviceName;
    private int signalNo;
    private String signalName;
    private int alarmLevel;
    private String startTime;
    private String endTime;

    public HisAlarmSyncRecord(int siteId, String siteName, int deviceId, String deviceName, int signalNo, String signalName, int alarmLevel, String startTime, String endTime) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.signalNo = signalNo;
        this.signalName = signalName;
        this.alarmLevel = alarmLevel;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
