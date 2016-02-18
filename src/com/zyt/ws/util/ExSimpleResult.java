package com.zyt.ws.util;

/**
 * Created by Administrator on 2015/9/12.
 */
public class ExSimpleResult extends SimpleResult {
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    private String tag;

    public ExSimpleResult(String tag, int result, String errMsg) {
        super(result, errMsg);
        this.tag = tag;
    }
}
