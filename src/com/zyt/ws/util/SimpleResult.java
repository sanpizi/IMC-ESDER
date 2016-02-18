package com.zyt.ws.util;

/**
 * Created by Administrator on 2015/8/15.
 */
public class SimpleResult {
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

    private int result;
    private String errMsg;

    public SimpleResult(int result, String errMsg) {
        this.result = result;
        this.errMsg = errMsg;
    }
}
