package com.zyt.ws.util;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ComplexResult {
    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ArrayList<ExSimpleResult> getRecordList() {
        return recordList;
    }

    public void setRecordList(ArrayList<ExSimpleResult> recordList) {
        this.recordList = recordList;
    }

    private int result;
    private String errMsg;
    private int totalRecords;
    private ArrayList<ExSimpleResult> recordList = new ArrayList<ExSimpleResult>();

    public ComplexResult(int result, String errMsg) {
        this.result = result;
        this.errMsg = errMsg;
    }

    public void done() {
        this.totalRecords = recordList.size();
    }

    public void addExSimpleResult(ExSimpleResult result) {
        recordList.add(result);
    }
}
