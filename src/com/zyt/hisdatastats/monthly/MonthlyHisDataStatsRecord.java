package com.zyt.hisdatastats.monthly;

/**
 * Created by Administrator on 2015/10/3.
 */
class MonthlyHisDataStatsRecord {
    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getSignalNo() {
        return signalNo;
    }

    public void setSignalNo(int signalNo) {
        this.signalNo = signalNo;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getAverage() {
        return average;
    }

    public void setAverage(float average) {
        this.average = average;
    }

    public float getAccumulatedValue() {
        return accumulatedValue;
    }

    public void setAccumulatedValue(float accumulatedValue) {
        this.accumulatedValue = accumulatedValue;
    }

    private int siteId;
    private int signalNo;
    private String month;
    private float maxValue;
    private float minValue;
    private int count;
    private float average;
    private float accumulatedValue;

    public MonthlyHisDataStatsRecord(int siteId, int signalNo, String month, float maxValue, float minValue, int count, float average, float acculatedValue) {
        this.siteId = siteId;
        this.signalNo = signalNo;
        this.month = month;
        this.maxValue = maxValue;
        this.minValue = minValue;
        this.count = count;
        this.average = average;
        this.accumulatedValue = acculatedValue;
    }
}
