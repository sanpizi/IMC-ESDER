package com.zyt.sites;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/8/1.
 */
class Site {
    private static final Logger logger = LogManager.getLogger(Site.class);

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAreaId() {
        return areaId;
    }

    public void setAreaId(int areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getX_pos() {
        return x_pos;
    }

    public void setX_pos(int x_pos) {
        this.x_pos = x_pos;
    }

    public int getY_pos() {
        return y_pos;
    }

    public void setY_pos(int y_pos) {
        this.y_pos = y_pos;
    }

    private int id;
    private String name;
    private String status;
    private int areaId;
    private String areaName;
    private int x_pos;
    private int y_pos;

    public Site(int id, String name, String status, int areaId, String areaName, int x_pos, int y_pos) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.areaId = areaId;
        this.areaName = areaName;
        this.x_pos = x_pos;
        this.y_pos = y_pos;
    }
}
