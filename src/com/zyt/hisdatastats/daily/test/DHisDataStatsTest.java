package com.zyt.hisdatastats.daily.test;

import com.zyt.hisdatastats.daily.DHisDataStats;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/10/3.
 */
public class DHisDataStatsTest {
    private static final Logger logger = LogManager.getLogger(DHisDataStatsTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        DHisDataStats data = new DHisDataStats(conn);
        conn.close();

        data.export();
    }
}
