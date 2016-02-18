package com.zyt.config.test;

import com.zyt.config.Config;
import com.zyt.config.ConfigDatas;
import com.zyt.ws.util.ComplexResult;
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
public class ConfigTest {
    private static final Logger logger = LogManager.getLogger(ConfigTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        ConfigDatas cds = Config.doQuery(conn, "1", "work mode");
        System.out.println(cds);
        conn.close();

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(cds));
        logger.debug("done of transferring to JSON string");
        System.out.flush();
    }
}
