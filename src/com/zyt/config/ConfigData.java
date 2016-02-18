package com.zyt.config;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ConfigData {
    private int devSn;
    private String signalName;
    private String value;

    public int getDevSn() {
        return devSn;
    }

    public void setDevSn(int devSn) {
        this.devSn = devSn;
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

    public ConfigData(int devSn, String signalName, String value) {
        this.devSn = devSn;
        this.signalName = signalName;
        this.value = value;
    }
}
