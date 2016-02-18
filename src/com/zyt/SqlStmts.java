package com.zyt;

/**
 * Created by Administrator on 2015/8/1.
 */
public final class SqlStmts {
    //for /globalStats
    public static final String GLOBALSTATS_SITE = "select a.site_id, " +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %1$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select b.SiteId from dbo.tab_site_rtdata b, dbo.tab_alarm_def c where b.DataAlarm=1 and b.SiteId=a.site_id and datediff(mi, b.datatime, getdate()) < %1$d and b.DevType=c.devType and b.DevSn=c.devSn and (b.SignalNo-1000)=c.typeId and b.DevType=163 and b.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%2$s))) then 'true' else 'false' end as alarm" +
            "  from dbo.tab_siteattr_WS a";
    public static final String GLOBALSTATS_ALARM = "select count(a.SiteId) as count, b.levelFlag " +
            "from dbo.tab_site_rtdata a, dbo.tab_alarm_def b " +
            "where a.devType=b.devType and a.devSn=b.devSn and (a.SignalNo-1000)=b.typeId and b.TypeId in (%1$s)" +
            "and a.DataAlarm=1 and a.DevType=163 and a.DataTime<>'1899-12-31 00:00:00.000' group by b.levelFlag";

    //for /areas
    public static final String AREAS = "select zone_id, zone_name from dbo.tab_zone_attr order by zone_id asc";

    //for /sites
    public static final String SITES = "with sitedata as (select row_number() over (order by %1$s %2$s) as rownumber, a.site_id, a.name," +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %3$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select d.SiteId from dbo.tab_site_rtdata d, dbo.tab_alarm_def c where d.DataAlarm=1 and d.SiteId=a.site_id and datediff(mi, d.datatime, getdate()) < %3$d and d.DevType=c.devType and d.DevSn=c.devSn and (d.SignalNo-1000)=c.typeId and d.DevType=163 and d.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%4$s))) then 'true' else 'false' end as alarm," +
            "  b.zone_id, b.zone_name, a.x_pos, a.y_pos" +
            "  from dbo.tab_siteattr a, dbo.tab_zone_attr b" +
            "  where a.zone_id=b.zone_id)" +
            "  select * from sitedata where rownumber >= %5$d and rownumber < %6$d %7$s";
    public static final String SITES_TOTALNUMBER = "with sitedata as (select a.site_id, a.name," +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %3$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select d.SiteId from dbo.tab_site_rtdata d, dbo.tab_alarm_def c where d.DataAlarm=1 and d.SiteId=a.site_id and datediff(mi, d.datatime, getdate()) < %3$d and d.DevType=c.devType and d.DevSn=c.devSn and (d.SignalNo-1000)=c.typeId and d.DevType=163 and d.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%4$s))) then 'true' else 'false' end as alarm," +
            "  b.zone_id, b.zone_name" +
            "  from dbo.tab_siteattr a, dbo.tab_zone_attr b" +
            "  where a.zone_id=b.zone_id)" +
            "  select count(*) as totalrecords from sitedata %5$s";

    public static final String SITES_WITH_AREAID_SPECIFIED = "with sitedata as (select row_number() over (order by %1$s %2$s) as rownumber, a.site_id, a.name," +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %3$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select d.SiteId from dbo.tab_site_rtdata d, dbo.tab_alarm_def c where d.DataAlarm=1 and d.SiteId=a.site_id and datediff(mi, d.datatime, getdate()) < %3$d and d.DevType=c.devType and d.DevSn=c.devSn and (d.SignalNo-1000)=c.typeId and d.DevType=163 and d.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%4$s))) then 'true' else 'false' end as alarm," +
            "  b.zone_id, b.zone_name, a.x_pos, a.y_pos" +
            "  from dbo.tab_siteattr a, dbo.tab_zone_attr b" +
            "  where a.zone_id=b.zone_id and a.zone_id in (%8$s))" +
            "  select * from sitedata where rownumber >= %5$d and rownumber < %6$d %7$s";
    public static final String SITES_WITH_AREAID_SPECIFIED_TOTALNUMBER = "with sitedata as (select a.site_id, a.name," +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %3$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select d.SiteId from dbo.tab_site_rtdata d, dbo.tab_alarm_def c where d.DataAlarm=1 and d.SiteId=a.site_id and datediff(mi, d.datatime, getdate()) < %3$d and d.DevType=c.devType and d.DevSn=c.devSn and (d.SignalNo-1000)=c.typeId and d.DevType=163 and d.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%4$s))) then 'true' else 'false' end as alarm," +
            "  b.zone_id, b.zone_name" +
            "  from dbo.tab_siteattr a, dbo.tab_zone_attr b" +
            "  where a.zone_id=b.zone_id and a.zone_id in (%6$s))" +
            "  select count(*) as totalrecords from sitedata %5$s";

    //for /site/{siteid}
    public static final String SITE_DETAILS_BRIEF = "select a.site_id, a.name," +
            "  case when (select datediff(mi, max(datatime), getdate()) from dbo.tab_site_rtdata where SiteId=a.site_id) < %1$d then 'Normal' else 'Offline' end as status," +
            "  case when (exists (select d.SiteId from dbo.tab_site_rtdata d, dbo.tab_alarm_def c where d.DataAlarm=1 and d.SiteId=a.site_id and datediff(mi, d.datatime, getdate()) < %1$d and d.DevType=c.devType and d.DevSn=c.devSn and (d.SignalNo-1000)=c.typeId and d.DevType=163 and d.DataTime<>'1899-12-31 00:00:00.000' and c.typeId in (%2$s))) then 'true' else 'false' end as alarm," +
            "  b.zone_id, b.zone_name, a.x_pos, a.y_pos" +
            "  from dbo.tab_siteattr a, dbo.tab_zone_attr b" +
            "  where a.zone_id=b.zone_id and a.site_id=%3$d";
    public static final String SITE_DETAILS_ALARMSTATS = "select count(a.SiteId) as count, b.levelFlag " +
            "from dbo.tab_site_rtdata a, dbo.tab_alarm_def b " +
            "where a.devType=b.devType and a.devSn=b.devSn and (a.SignalNo-1000)=b.typeId and a.DevType=163 and a.DataTime<>'1899-12-31 00:00:00.000' and b.typeId in (%1$s)" +
            "and a.DataAlarm=1 and a.SiteId=%2$d group by b.levelFlag";
    public static final String SITE_DETAILS_SIGNALS = "select SignalNo, DataVal, DataTime from dbo.tab_site_rtdata" +
            " where SiteId=%1$d and SignalNo in (%2$s)";

    //for daily alarm stats
    public static final String SELECT_DAILY_ALARM_STATS = "select convert(varchar(10), a.tmOccured, 120) as occuredDate," +
            "  sum(case when b.levelFlag = 4 then 1 else 0 end) as fatalcount," +
            "  sum(case when b.levelFlag = 3 then 1 else 0 end) as urgentcount," +
            "  sum(case when b.levelFlag = 2 then 1 else 0 end) as importantcount," +
            "  sum(case when b.levelFlag = 1 then 1 else 0 end) as generalcount" +
            "  from dbo.tab_alarm_data a, dbo.tab_alarm_def b" +
            "  where a.devType=b.devType and a.devSn=b.devSn and a.typeId=b.typeId and a.DevType=163 and a.state=1 and b.typeId in (%1$s)" +
            "  and a.tmOccured >= '%2$s'" +
            "  and a.tmOccured <= '%3$s'" +
            "  group by convert(varchar(10), a.tmOccured, 120) order by convert(varchar(10), a.tmOccured, 120) asc";

    //for authentication
    public static final String SELECT_USER_DETAILS_BASED_ON_NAME = "select * from dbo.tab_userdata where clerkName=?";
    public static final String SELECT_USER_DETAILS_BASED_ON_ID = "select * from dbo.tab_userdata where ID=?";

    //for modify account
    public static final String MODIFY_USER_PSWD = "update dbo.tab_userdata set password=? where clerkName=?";

    //for alarms query
    // /alarms
    public static final String SELECT_REALTIME_ALARMS_2005 = "with alarmdata as (select row_number() over" +
            "  (order by %1$s %2$s) as rownumber, c.zone_id, d.zone_name, c.site_id, c.name, b.typeId, a.SignalName, b.levelFlag, a.DataTime" +
            "  from dbo.Tab_site_rtdata a, dbo.Tab_alarm_def b, Tab_siteattr_WS c, tab_zone_attr d" +
            "  where a.DevType=b.devType and a.DevSn=b.devSn and (a.SignalNo-1000)=b.typeId and a.SiteId=c.site_id and c.zone_id=d.zone_id" +
            "  and a.DevType=163 and a.DataAlarm=1 and b.typeId in (%3$s)" +
            "  and a.DataTime<>'1899-12-31 00:00:00.000' %4$s)" +
            "  select rownumber, zone_id, zone_name, site_id, name, typeId, SignalName," +
            "  case levelFlag when 4 then 'Critical' when 3 then 'Major' when 2 then 'Minor' when 1 then 'Warning' end as severity," +
            "  DataTime from alarmdata where rownumber >= %5$d and rownumber < %6$d";
    public static final String SELECT_REALTIME_ALARMS_2005_TOTALNUMBER = "select count(*)" +
            "  from dbo.Tab_site_rtdata a, dbo.Tab_alarm_def b, dbo.Tab_siteattr_WS c, dbo.tab_zone_attr d" +
            "  where a.DevType=b.devType and a.DevSn=b.devSn and (a.SignalNo-1000)=b.typeId and a.SiteId=c.site_id and c.zone_id=d.zone_id" +
            "  and a.DevType=163 and a.DataAlarm=1 and b.typeId in (%1$s) " +
            "  and a.DataTime<>'1899-12-31 00:00:00.000' " +
            "  %2$s";
    // /hisalarms
    public static final String SELECT_HIS_ALARMS_2005 = "with alarmdata as (select top %7$d row_number() over" +
            "  (order by %1$s %2$s) as rownumber, c.zone_id, d.zone_name, c.site_id, c.name, a.typeId, b.signalName, a.tmOccured, a.tmRestored, b.levelFlag" +
            "  from dbo.tab_alarm_data a, dbo.Tab_alarm_def b, Tab_siteattr_WS c, tab_zone_attr d" +
            "  where a.DevType=b.devType and a.DevSn=b.devSn and a.typeId=b.typeId" +
            "  and a.siteId=c.site_id and c.zone_id=d.zone_id and a.DevType=163 and a.tmRestored is not null and a.typeId in (%3$s) %4$s)" +
            "  select rownumber, zone_id, zone_name, site_id, name, typeId, signalName," +
            "  case levelFlag when 4 then 'Critical' when 3 then 'Major' when 2 then 'Minor' when 1 then 'Warning' end as severity," +
            "  tmOccured, tmRestored " +
            "  from alarmdata";

    //for config
    public static final String SELECT_DEVSN_BASED_ON_SITEID = "select dev_id, site_id from tab_dtu_devattr where site_id in (%1$s)";
    //do config
    public static final String SELECT_OID = "select oid, expression from dbo.tab_site_OidConfigData where signalName = %1$s";
    //do query
    public static final String SELECT_VALUE = "select nDevSn, signalName, case defaultVal when null then '-' when '' then '-' else defaultVal end as value from dbo.tab_site_OidConfigData where signalName= %1$s and nDevSn in (%2$s)";
    public static final String SELECT_VALUE_FOR_ALL_SIGNALS = "select nDevSn, signalName, case defaultVal when null then '-' when '' then '-' else defaultVal end as value from dbo.tab_site_OidConfigData where signalName in (%1$s) and nDevSn in (%2$s)";

    //hisdatas
    public static final String SELECT_HIS_DATAS_2005 = "with hisdata as (select top %7$d row_number() over (order by %1$s %2$s) as rownumber, c.zone_id, c.zone_name," +
            "  b.site_id, b.name, a.signalNo, a.SignalName, a.DataVal, a.DataTime" +
            "  from tab_site_hisdata a, Tab_siteattr_WS b, tab_zone_attr c" +
            "  where a.SiteId=b.site_id and b.zone_id=c.zone_id and a.signalNo in (%3$s) %4$s)" +
            "  select rownumber, zone_id, zone_name, site_id, name, signalNo, SignalName, DataVal, DataTime from hisdata";  // +  //modified on 2015.11.18

    //Monthly hisdata statistics
    public static final String HISDATA_STATS_HOURLY = "select SiteId, SignalNo, convert(varchar(13), DataTime, 120) as hour," +
            "  max( cast( ltrim(DataVal) as float)  ) as maxvalue," +
            "  min( cast (ltrim(DataVal) as float) ) as minvalue," +
            "  count(*) as count, " +
            "  avg( cast (ltrim(DataVal) as float) ) as average, SiteName" +
            "  from tab_site_hisdata" +
            "  where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  group by SiteId, SignalNo, convert(varchar(13), DataTime, 120), SiteName";
    public static final String HISDATA_STATS_HOURLY_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(13), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and convert(varchar(19), DataTime, 120) like '____-__-__ __:00:00'";

    public static final String HISDATA_STATS_DAILY = "select SiteId, SignalNo, convert(varchar(10), DataTime, 120) as day," +
            "  max( cast( ltrim(DataVal) as float)  ) as maxvalue," +
            "  min( cast (ltrim(DataVal) as float) ) as minvalue," +
            "  count(*) as count, " +
            "  avg( cast (ltrim(DataVal) as float) ) as average, SiteName" +
            "  from tab_site_hisdata" +
            "  where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  group by SiteId, SignalNo, convert(varchar(10), DataTime, 120), SiteName";
    public static final String HISDATA_STATS_DAILY_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(10), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and convert(varchar(19), DataTime, 120) like '____-__-__ 00:00:00'" +
            "  order by DataTime, SiteName, SignalName";

        public static final String HISDATA_STATS_MONTHLY = "select SiteId, SignalNo, convert(varchar(7), DataTime, 120) as month," +
                "  max( cast( ltrim(DataVal) as float)  ) as maxvalue," +
                "  min( cast (ltrim(DataVal) as float) ) as minvalue," +
                "  count(*) as count, " +
                "  avg( cast (ltrim(DataVal) as float) ) as average, SiteName" +
                "  from tab_site_hisdata" +
                "  where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
                "  and DataTime >= '%2$04d-%3$02d-01 00:00:00.000' and DataTime < '%4$04d-%5$02d-01 00:00:00.000'" +
                "  group by SiteId, SignalNo, convert(varchar(7), DataTime, 120), SiteName";
        public static final String HISDATA_STATS_MONTHLY_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(7), DataTime, 120) as datetime, DataVal, SiteName" +
                "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
                "  and DataTime >= '%2$04d-%3$02d-01 00:00:00.000' and DataTime <= '%4$04d-%5$02d-01 00:00:00.000'" +
                "  and convert(varchar(19), DataTime, 120) like '____-__-01 00:00:00'" +
                "  order by DataTime, SignalNo";

        //Daily hisdata statistics
        public static final String DAILY_HISDATA_STATS_1MINUTE = "with hisdata as (select SiteId, SignalNo," +
                "  (convert(varchar(10), DataTime, 120) + ' ' + right('00' + convert(varchar, datepart(hh, convert(varchar(16), DataTime, 120))), 2) + ':' +" +
                "    right('00' + convert(varchar, datepart(mi, convert(varchar(16), DataTime, 120))), 2)) as datetime," +
                "  cast(ltrim(DataVal) as float) as value, SiteName from tab_site_hisdata" +
                "  where ltrim(DataVal)<>'-' and SignalNo in ( %1$s )" +
                "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
                "  and save_interval=1) select SiteId, SignalNo, datetime, max(value) as maxvalue, min(value) as minvalue, count(*) as count, avg(value) as average, SiteName" +
                "  from hisdata group by SiteId, SignalNo, datetime, SiteName";// order by datetime, SiteId, SignalNo";
    public static final String DAILY_HISDATA_STATS_1MINUTE_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(16), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=1 and convert(varchar(19), DataTime, 120) like '____-__-__ __:__:00'" +
            "  order by DataTime, SiteName, SignalName";

    public static final String DAILY_HISDATA_STATS_15MINUTE = "with hisdata as (select SiteId, SignalNo," +
            "  (convert(varchar(10), DataTime, 120) + ' ' + right('00' + convert(varchar, datepart(hh, convert(varchar(16), DataTime, 120))), 2) + ':' +" +
            "    right('00' + convert(varchar, (datepart(mi, convert(varchar(16), DataTime, 120)) - datepart(mi, convert(varchar(16), DataTime, 120)) %% 15)), 2)) as datetime," +
            "  cast(ltrim(DataVal) as float) as value, SiteName from tab_site_hisdata" +
            "  where ltrim(DataVal)<>'-' and SignalNo in ( %1$s )" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=15) select SiteId, SignalNo, datetime, max(value) as maxvalue, min(value) as minvalue, count(*) as count, avg(value) as average, SiteName" +
            "  from hisdata group by SiteId, SignalNo, datetime, SiteName";// order by datetime, SiteId, SignalNo";
    public static final String DAILY_HISDATA_STATS_15MINUTE_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(16), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=15 and convert(varchar(19), DataTime, 120) like '____-__-__ __:[0134][05]:00'" +
            "  order by DataTime, SiteName, SignalName";

    public static final String DAILY_HISDATA_STATS_30MINUTE = "with hisdata as (select SiteId, SignalNo," +
            "  (convert(varchar(10), DataTime, 120) + ' ' + right('00' + convert(varchar, datepart(hh, convert(varchar(16), DataTime, 120))), 2) + ':' +" +
            "    right('00' + convert(varchar, (datepart(mi, convert(varchar(16), DataTime, 120)) - datepart(mi, convert(varchar(16), DataTime, 120)) %% 30)), 2)) as datetime," +
            "  cast(ltrim(DataVal) as float) as value, SiteName from tab_site_hisdata" +
            "  where ltrim(DataVal)<>'-' and SignalNo in ( %1$s )" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=30) select SiteId, SignalNo, datetime, max(value) as maxvalue, min(value) as minvalue, count(*) as count, avg(value) as average, SiteName" +
            "  from hisdata group by SiteId, SignalNo, datetime, SiteName";// order by datetime, SiteId, SignalNo";
    public static final String DAILY_HISDATA_STATS_30MINUTE_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(16), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=30 and convert(varchar(19), DataTime, 120) like '____-__-__ __:[03]0:00'" +
            "  order by DataTime, SiteName, SignalName";

    public static final String DAILY_HISDATA_STATS_60MINUTE = "with hisdata as (select SiteId, SignalNo," +
            "  (convert(varchar(10), DataTime, 120) + ' ' + right('00' + convert(varchar, datepart(hh, convert(varchar(16), DataTime, 120))), 2) + ':' +" +
            "    right('00' + convert(varchar, (datepart(mi, convert(varchar(16), DataTime, 120)) - datepart(mi, convert(varchar(16), DataTime, 120)) %% 60)), 2)) as datetime," +
            "  cast(ltrim(DataVal) as float) as value, SiteName from tab_site_hisdata" +
            "  where ltrim(DataVal)<>'-' and SignalNo in ( %1$s )" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime < '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=60) select SiteId, SignalNo, datetime, max(value) as maxvalue, min(value) as minvalue, count(*) as count, avg(value) as average, SiteName" +
            "  from hisdata group by SiteId, SignalNo, datetime, SiteName";// order by datetime, SiteId, SignalNo";
    public static final String DAILY_HISDATA_STATS_60MINUTE_ACCUMULATED = "select SiteId, SignalNo, convert(varchar(16), DataTime, 120) as datetime, DataVal, SiteName" +
            "  from Tab_site_hisdata where ltrim(DataVal)<>'-' and SignalNo in (%1$s)" +
            "  and DataTime >= '%2$04d-%3$02d-%4$02d 00:00:00.000' and DataTime <= '%5$04d-%6$02d-%7$02d 00:00:00.000'" +
            "  and save_interval=60 and convert(varchar(19), DataTime, 120) like '____-__-__ __:00:00'" +
            "  order by DataTime, SiteName, SignalName";

    //data synchronization
    public static final String DATA_SYNC_READ = "select SiteId, SiteName, DevSn, DevName, SignalNo, SignalName," +
            "  ltrim(DataVal) as value, DataTime from Tab_site_hisdata" +
            "  where DataTime >= '%1$04d-%2$02d-%3$02d 00:00:00.000'" +
            "  and DataTime < '%4$04d-%5$02d-%6$02d 00:00:00.000'" +
            "  and SignalNo in (%7$s)";
    public static final String DATA_SYNC_WRITE = "insert into historydata (SITEID, SITENAME, DEVICEID, DEVICENAME," +
            "  SIGNALNO, SIGNALNAME, DATAVALUE, DATATIME)" +
            "  values (%1$d, '%2$s', %3$d, '%4$s', %5$d, '%6$s', '%7$s', '%8$s')";
    public static final String ALARM_SYNC_READ = "select a.siteId, b.name, a.devSn, a.devName, a.typeId," +
            "  c.signalName, c.levelFlag, a.tmOccured, a.tmRestored" +
            "  from tab_alarm_data a, tab_siteattr_ws b, tab_alarm_def c" +
            "  where a.siteId=b.site_id and a.DevType=c.devType and a.devSn=c.devSn and a.typeId=c.typeId" +
            "  and a.devType=163 and a.typeId in (%1$s) and a.tmRestored is not NULL" +
            "  and a.tmOccured >= '%2$04d-%3$02d-%4$02d 00:00:00.000'" +
            "  and a.tmOccured < '%5$04d-%6$02d-%7$02d 00:00:00.000'";
    public static final String ALARM_SYNC_WRITE = "insert into alarmdata (SITEID, SITENAME, DEVICEID, DEVICENAME," +
            "  SIGNALNO, SIGNALNAME, ALARMLEVEL, STARTTIME, ENDTIME)" +
            "  values (%1$d, '%2$s', %3$d, '%4$s', %5$d, '%6$s', %7$d, '%8$s', '%9$s')";

    private SqlStmts() {
    }
}
