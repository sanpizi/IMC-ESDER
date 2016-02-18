package com.zyt.ws.util;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/1.
 */
public class ZYTWSConnection {
    public int getId() {
        return id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    private int id;
    private boolean available = true;
    private Connection conn;

    public ZYTWSConnection(int id, Connection conn) {
        this.id = id;
        this.conn = conn;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
            }
        }
    }
}
