package com.zyt.alarm.test;

import com.zyt.alarm.HisAlarms;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/21.
 */
public class HisAlarmsTest {
    private static final Logger logger = LogManager.getLogger(HisAlarmsTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        HisAlarms data = new HisAlarms(conn, 0, -1, null, null,
                "Critical", -1, null, -1, null, -1, null, null, null);
        conn.close();

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");
        System.out.flush();

        FileOutputStream out = new FileOutputStream("hisalarms.xlsx");
        data.export(out);
        out.close();
    }
}
