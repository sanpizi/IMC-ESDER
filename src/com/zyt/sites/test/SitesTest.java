package com.zyt.sites.test;

import com.zyt.sites.Sites;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/1.
 */
public class SitesTest {
    private static final Logger logger = LogManager.getLogger(SitesTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        Sites sites = new Sites(conn, 0, 5, null, null, null, new String[]{"1", "2"});
        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(sites));
        logger.debug("done of transferring to JSON string");

        sites = new Sites(conn, 0, 5, null, null, "Offline");
        logger.debug("start transferring to JSON string");
        System.out.println(mapper.writeValueAsString(sites));
        logger.debug("done of transferring to JSON string");

        conn.close();
        System.out.flush();
    }
}
