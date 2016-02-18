package com.zyt.ws.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by Administrator on 2015/8/1.
 */
public class ZYTWSConnPool {
    private int count;
    private boolean bClosed = false;
    private Semaphore semaphore;
    private HashMap<Integer, ZYTWSConnection> connMap;

    private String connURL;

    public ZYTWSConnPool(int count, String connURL) throws SQLException {
        this.count = count;
        this.connURL = connURL;
        this.semaphore = new Semaphore(count);
        connMap = new HashMap<Integer, ZYTWSConnection>();

        for (int i = 0; i < count; i++) {
            connMap.put(i, new ZYTWSConnection(i, DriverManager.getConnection(connURL)));
        }
    }

    public ZYTWSConnection getConn() {
        if (bClosed) {
            return null;
        }
        try {
            semaphore.acquire();
            for (Map.Entry<Integer,ZYTWSConnection> entry : connMap.entrySet()) {
                ZYTWSConnection conn = entry.getValue();
                if (conn.isAvailable()) {
                    conn.setAvailable(false);
                    return conn;
                }
            }
        } catch (InterruptedException e) {
        }
        return null;
    }

    public void releaseConn(ZYTWSConnection conn) {
        connMap.get(conn.getId()).setAvailable(true);
        semaphore.release();
    }

    public void close() {
        bClosed = true;
        for (Map.Entry<Integer, ZYTWSConnection> entry : connMap.entrySet()) {
            entry.getValue().close();
        }
    }
}
