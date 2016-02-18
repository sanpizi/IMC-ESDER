package com.zyt.sitedetail.test;

import com.zyt.sitedetail.SiteDetail;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/12.
 */
public class SiteDetailTest {
    private static final Logger logger = LogManager.getLogger(SiteDetailTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        SiteDetail data = new SiteDetail(conn, 1);
        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");

        conn.close();
        System.out.flush();
    }
}
