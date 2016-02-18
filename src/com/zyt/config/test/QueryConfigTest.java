package com.zyt.config.test;

import com.zyt.config.Config;
import com.zyt.config.ConfigDatas;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/9/12.
 */
public class QueryConfigTest {
    private static final Logger logger = LogManager.getLogger(QueryConfigTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        ConfigDatas data = Config.doQuery(conn, "1,2,3", "T3");
        System.out.println(data);
        conn.close();

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");
        System.out.flush();
    }
}
