package com.zyt.alarm.test;

import com.zyt.alarm.DailyAlarmStats;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/9.
 */
public class DailyAlarmStatsTest {
    private static final Logger logger = LogManager.getLogger(DailyAlarmStats.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        DailyAlarmStats stats = new DailyAlarmStats("2015-05-01", "2015-06-01", conn);
        conn.close();

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(stats));
        logger.debug("done of transferring to JSON string");
        System.out.flush();
    }
}
