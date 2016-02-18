package com.zyt.user.test;

import com.zyt.user.UserDetails;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/15.
 */
public class UserDetailsTest {
    private static final Logger logger = LogManager.getLogger(UserDetailsTest.class);

    public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionURL = "jdbc:sqlserver://localhost:9999;databaseName=sdimc;user=sa;password=tong1218";
        Connection conn = DriverManager.getConnection(connectionURL);
        System.out.println(conn);

        UserDetails data = new UserDetails(conn, 0);
        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");

        data = new UserDetails(conn, "Admin");
        logger.debug("start transferring to JSON string");
        System.out.println(mapper.writeValueAsString(data));
        logger.debug("done of transferring to JSON string");

        logger.debug("pswd " + data.getPassword());

        conn.close();
        System.out.flush();
    }
}
