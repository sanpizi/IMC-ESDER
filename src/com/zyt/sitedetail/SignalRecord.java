package com.zyt.sitedetail;

/**
 * Created by Administrator on 2015/10/21.
 */
public class SignalRecord {
    public int getSignalId() {
        return signalId;
    }

    public void setSignalId(int signalId) {
        this.signalId = signalId;
    }

    public String getDataVal() {
        return dataVal;
    }

    public void setDataVal(String dataVal) {
        this.dataVal = dataVal;
    }

    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    private int signalId;
    private String dataVal;
    private String dataTime;

    public SignalRecord(int signalId, String dataVal, String dataTime) {
        this.signalId = signalId;
        this.dataVal = dataVal;
        this.dataTime = dataTime;
    }
}
