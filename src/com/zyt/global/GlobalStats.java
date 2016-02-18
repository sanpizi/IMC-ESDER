package com.zyt.global;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;

/**
 * Created by Administrator on 2015/8/11.
 */
public class GlobalStats {
    private static final Logger logger = LogManager.getLogger(GlobalStats.class);

    public SiteStats getSites() {
        return sites;
    }

    public void setSites(SiteStats sites) {
        this.sites = sites;
    }

    public AlarmStats getAlarms() {
        return alarms;
    }

    public void setAlarms(AlarmStats alarms) {
        this.alarms = alarms;
    }

    private SiteStats sites;
    private AlarmStats alarms;

    public GlobalStats(Connection conn) {
        sites = new SiteStats(conn);
        alarms = new AlarmStats(conn);
    }
}
