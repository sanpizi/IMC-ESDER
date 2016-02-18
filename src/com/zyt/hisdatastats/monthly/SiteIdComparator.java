package com.zyt.hisdatastats.monthly;

import com.zyt.Util;

import java.util.Comparator;

/**
 * Created by Administrator on 2015/10/3.
 */
public class SiteIdComparator implements Comparator<String> {
    public int compare(java.lang.String key1, java.lang.String key2) {
        int siteId1 = Integer.parseInt(MHisDataStats.getSiteId(key1));
        int siteId2 = Integer.parseInt(MHisDataStats.getSiteId(key2));
        return siteId1 - siteId2;
    }
}
