package com.zyt.hisdata.test;

import com.zyt.hisdata.HisDatas;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/9/28.
 */
public class HisDatasTest {
    private static final Logger logger = LogManager.getLogger(HisDatasTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        HisDatas data = new HisDatas(conn, 1, 1, "time", "desc", -1, null, -1, null, -1, null, 60, null, null);
        conn.close();

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");
        System.out.flush();
    }
}
