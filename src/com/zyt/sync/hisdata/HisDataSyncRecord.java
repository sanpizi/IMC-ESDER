package com.zyt.sync.hisdata;

/**
 * Created by Administrator on 2015/10/4.
 */
class HisDataSyncRecord {
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

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    private int siteId;
    private String siteName;
    private int deviceId;
    private String deviceName;
    private int signalNo;
    private String signalName;
    private String dataValue;
    private String timeStamp;

    public HisDataSyncRecord(int siteId, String siteName, int deviceId, String deviceName, int signalNo, String signalName, String dataValue, String timeStamp) {
        this.siteId = siteId;
        this.siteName = siteName;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.signalNo = signalNo;
        this.signalName = signalName;
        this.dataValue = dataValue;
        this.timeStamp = timeStamp;
    }
}
