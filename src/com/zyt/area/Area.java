package com.zyt.area;

/**
 * Created by Administrator on 2015/8/12.
 */
public class Area {
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

    private int id;
    private String name;

    public Area(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
