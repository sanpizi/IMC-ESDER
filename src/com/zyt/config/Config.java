package com.zyt.config;

import com.zyt.SqlStmts;
import com.zyt.Util;
import com.zyt.ws.BootstrapServlet;
import com.zyt.ws.util.ComplexResult;
import com.zyt.ws.util.ExSimpleResult;
import com.zyt.ws.util.Validation;
import com.zyt.ws.util.ValidationType;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.snmp4j.PDU;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2015/9/12.
 */
public class Config {
    private static final Logger logger = LogManager.getLogger(BootstrapServlet.class);
    private static String ALL_SIGNALS = "";

    private static final String[] AVAILABLE_SIGNALS = new String[]{"FCS Reset",
            "A/C 1 Control", "A/C 2 Control",
            "Fan Control", "Turn Off Fan UnderVoltage",
            "Temp Diff between Indoor and Outdoor", "Over Humidity",
            "Humidity Limit ON/OFF", "Indoor Under Temp",
            "Indoor Over Temp", "A/C Minimum Duration",
            "Work Mode", "Fan Start Temp",
            "A/C Start Temp", "Emergency On Temp",
            "Turn Off Fan OverHumidity", "Fan Minimum Duration",
            "Fan Speed Control", "DC Under Voltage",
            "Interval of Storing Data"  //add the new configuration item 2015.12.05
    };
    private static final Validation[] VALIDATIONS = new Validation[]{new Validation(ValidationType.NOLIMIT),
            new Validation(ValidationType.ENUM, 0, 1), new Validation(ValidationType.ENUM, 0, 1),
            new Validation(ValidationType.ENUM, 0, 1), new Validation(ValidationType.ENUM, 0, 1),
            new Validation(ValidationType.RANGE, 1, 5), new Validation(ValidationType.RANGE, 0, 100),
            new Validation(ValidationType.NOLIMIT), new Validation(ValidationType.RANGE, -20, 80),
            new Validation(ValidationType.RANGE, -20, 80), new Validation(ValidationType.NOLIMIT),
            new Validation(ValidationType.ENUM, 0, 1, 2, 3, 4), new Validation(ValidationType.RANGE, 20, 29),
            new Validation(ValidationType.LOWVARRANGE, 6, 37), new Validation(ValidationType.LOWVARRANGE, 3, 44),
            new Validation(ValidationType.ENUM, 0, 1), new Validation(ValidationType.RANGE, 1, 10),
            new Validation(ValidationType.RANGE, 0, 100), new Validation(ValidationType.RANGE, 0, 60),
            new Validation(ValidationType.ENUM, 1, 15, 30, 60)
    };
    private static final String[] LOWVARS = new String[]{null, null, null,
            null, null, null, null, null, null, null, null,
            null, null, "Fan Start Temp", "A/C Start Temp", null, null, null, null,
            null
    };

    static {
        StringBuilder sbuf = new StringBuilder(1024);
        for (int i = 0; i < AVAILABLE_SIGNALS.length; i++) {
            sbuf.append('\'').append(AVAILABLE_SIGNALS[i]).append("\',");
        }
        ALL_SIGNALS = sbuf.substring(0, sbuf.length() - 1);
    }

    public static int getSignalIndex(String signalName) {
        for (int i = 0; i < AVAILABLE_SIGNALS.length; i++) {
            if (AVAILABLE_SIGNALS[i].equalsIgnoreCase(signalName)) {
                return i;
            }
        }
        logger.debug("invalid signalName " + signalName);
        return -1;
    }

    public static boolean isValidValue(Connection conn, String siteIdStr, int signalIndex, String value) {
        switch (VALIDATIONS[signalIndex].getVt()) {
            case NOLIMIT:
                return true;
            case ENUM:
            case RANGE:
                try {
                    return VALIDATIONS[signalIndex].isValid(Integer.parseInt(value));
                } catch (NumberFormatException e) {
                    logger.debug("invalid value");
                    return false;
                }
            case LOWVARRANGE:
                int newValue = -1;
                try {
                    newValue = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    return false;
                }
                ConfigDatas data = Config.doQuery(conn, siteIdStr, LOWVARS[signalIndex]);
                if (data.getRecordList().isEmpty()) {
                    logger.debug("cannot find current value");
                    return false;
                }
                int curValue = -1;
                try {
                    curValue = Integer.parseInt(data.getRecordList().get(0).getValue());
                } catch (NumberFormatException e) {
                    logger.debug("invalid current value");
                }
                return VALIDATIONS[signalIndex].isValid(newValue, curValue);
        }
        return false;
    }

    public static ComplexResult doConfig(Connection conn, String siteIdStr, String signalName, String value) {
        int signalIndex = getSignalIndex(signalName);
        if (signalIndex == -1) {
            return new ComplexResult(1, "invalid signalName");
        }

        if (!isValidValue(conn, siteIdStr, signalIndex, value)) {
            return new ComplexResult(2, "invalid new value");
        }

        ArrayList<Integer> devSnList = new ArrayList<Integer>();
        HashMap<Integer, String> devSn2SiteIdMap = new HashMap<Integer, String>();
        String queryDevSnStr = String.format(SqlStmts.SELECT_DEVSN_BASED_ON_SITEID, siteIdStr);
        logger.debug("queryDevSnStr is " + queryDevSnStr);
        Statement stmt = null;
        ResultSet rs = null;
        logger.debug("start querying devSn list");
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryDevSnStr);
            while (rs.next()) {
                int devSn = rs.getInt(1);
                int siteId = rs.getInt(2);
                devSnList.add(devSn);
                devSn2SiteIdMap.put(devSn, Integer.toString(siteId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
        logger.debug("end of querying devSn list");

        String oid = null;
        int expression = 0;
        String queryStr = String.format(SqlStmts.SELECT_OID, "'" + signalName + "'");
        logger.debug("queryStr is " + queryStr);
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStr);
            if (rs.next()) {
                oid = rs.getString(1);
                expression = rs.getInt(2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }

        if (oid == null) {
            logger.error("cannot find corresponding oid");
            return new ComplexResult(1, "cannot find correspoding oid");
        }

        ComplexResult er = new ComplexResult(0, "succeeded");
        boolean bPartialSucceeded = false;

        logger.debug("start of doing config");
        for (Integer devSn : devSnList) {
            String siteIdString = devSn2SiteIdMap.get(devSn);
            logger.debug("try to do config on " + devSn + " for " + signalName + " with value " + value);
            PDU resp = BootstrapServlet.setOidValue(oid + "." + devSn, value, expression);
            if (resp == null) {
                er.addExSimpleResult(new ExSimpleResult(siteIdString, 1, "Timeout or wrong value"));
                bPartialSucceeded = true;
            } else {
                if (resp.getErrorStatus() == PDU.noError) {
                    er.addExSimpleResult(new ExSimpleResult(siteIdString, 0, "succeeded"));
                } else {
                    er.addExSimpleResult(new ExSimpleResult(siteIdString, resp.getErrorStatus(), resp.getErrorStatusText()));
                    bPartialSucceeded = true;
                }
            }
        }
        er.done(); //set count
        logger.debug("end of doing config");
        if (bPartialSucceeded) {
            er.setErrMsg("partial succeeded");
        }
        return er;
    }

    public static ConfigDatas doQuery(Connection conn, String siteIdStr, String signalName) {
        ConfigDatas data = new ConfigDatas();
        if (signalName != null && getSignalIndex(signalName) == -1) {
            data.setTotalRecords(0);
            return data;
        }

        StringBuilder devSnStrBuf = new StringBuilder();
        String queryDevSnStr = String.format(SqlStmts.SELECT_DEVSN_BASED_ON_SITEID, siteIdStr);
        logger.debug("queryDevSnStr is " + queryDevSnStr);
        Statement stmt = null;
        ResultSet rs = null;
        logger.debug("start querying devSn list");
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryDevSnStr);
            while (rs.next()) {
                devSnStrBuf.append(rs.getInt(1)).append(',');
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
        String devSnStr = "";
        if (devSnStrBuf.length() > 0) {
            devSnStr = devSnStrBuf.substring(0, devSnStrBuf.length() - 1);
        }
        logger.debug("end of querying devSn list, got " + devSnStr);

        String queryStr = null;
        if (signalName != null) {
            queryStr = String.format(SqlStmts.SELECT_VALUE, "'" + signalName + "'", devSnStr);
        } else {
            queryStr = String.format(SqlStmts.SELECT_VALUE_FOR_ALL_SIGNALS, ALL_SIGNALS, devSnStr);
        }
        logger.debug("queryStr is " + queryStr);
        logger.debug("start querying config data");
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(queryStr);
            while (rs.next()) {
                int sn = rs.getInt(1);
                String sigName = rs.getString(2).trim();
                String sigValue = rs.getString(3).trim();
                if (sigName.equalsIgnoreCase("A/C 1 Control") || sigName.equalsIgnoreCase("A/C 2 Control")  //modified on 2016.01.04
                        || sigName.equalsIgnoreCase("Fan Control")) {
                    if (sigValue.equalsIgnoreCase("Run")) {
                        sigValue = "1";
                    } else if (sigValue.equalsIgnoreCase("Stop")) {
                        sigValue = "0";
                    }
                }
                data.addConfigData(new ConfigData(sn, sigName, sigValue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
        data.done();
        logger.debug("end of querying config data");
        return data;
    }
}
