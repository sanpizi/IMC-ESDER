package com.zyt.config;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ConfigDatas {
    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<ConfigData> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<ConfigData> recordList) {
        this.recordList = recordList;
    }

    private int totalRecords;
    private ArrayList<ConfigData> recordList = new ArrayList<ConfigData>();

    public void addConfigData(ConfigData data) {
        recordList.add(data);
    }

    public void done() {
        totalRecords = recordList.size();
    }
}
