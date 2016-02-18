package com.zyt.area;

import com.zyt.SqlStmts;
import com.zyt.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/8/12.
 */
public class Areas {
    private static final Logger logger = LogManager.getLogger(Areas.class);

    public ArrayList<Area> getAreaList() {
        return areaList;
    }

    public void setAreaList(ArrayList<Area> areaList) {
        this.areaList = areaList;
    }

    private ArrayList<Area> areaList = new ArrayList<Area>(256);

    public Areas(Connection conn) {
        Statement stmt = null;
        ResultSet rs = null;

        try {
            stmt = conn.createStatement();
            logger.debug("start querying areas");
            rs = stmt.executeQuery(SqlStmts.AREAS);
            while (rs.next()) {
                areaList.add(new Area(rs.getInt(1), rs.getString(2)));
            }
            logger.debug("end of querying areas");
            Util.safeClose(rs, stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }
}
