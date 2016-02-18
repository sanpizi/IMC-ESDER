package com.zyt.hisdata;

/**
 * Created by Administrator on 2015/9/28.
 */
public class HisDataRecord {
    public int getZoneId() {
        return zoneId;
    }

    public void setZoneId(int zoneId) {
        this.zoneId = zoneId;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private int zoneId;
    private String zoneName;
    private int siteId;
    private String siteName;
    private int signalId;
    private String signalName;
    private String value;
    private String time;

    public HisDataRecord(int zoneId, String zoneName, int siteId, String siteName, int signalId,
                         String signalName, String value, String time) {
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.siteId = siteId;
        this.siteName = siteName;
        this.signalId = signalId;
        this.signalName = signalName;
        this.value = value;
        this.time = time;
    }
}
