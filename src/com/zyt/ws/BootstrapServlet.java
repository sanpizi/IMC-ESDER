package com.zyt.ws;

import com.zyt.Util;
import com.zyt.job.ZYTScheduler;
import com.zyt.ws.util.ZYTWSConnPool;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Administrator on 2015/8/1.
 */
@WebServlet(name = "BootstrapServlet")
public class BootstrapServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(BootstrapServlet.class);

    private static String dbHost;
    private static int dbPort;
    private static String dbName;
    private static String userName;
    private static String password;

    private static int siteOnlineInternalInMinutes = 15;

    private static String connURL;

    private static int connPoolCount = 5;
    private static ZYTWSConnPool connPool;

    private static int snmpVer = 2;
    private static String snmpCommunity = "public";
    private static String snmpTransport = "udp";
    private static String snmpHost = "127.0.0.1";
    private static int snmpPort = 161;
    private static int snmpTimeout = 3000; //3s
    private static int snmpRetries = 1;

    private static Snmp snmp;
    private static CommunityTarget target;

    private static String statsFilesFolder = "statsFiles";
    private static int dailyStatsFilesCount = 30;
    private static int monthlyStatsFilesCount = 12;

    private static String oracleIp = "10.1.32.21";
    private static int oraclePort = 1521;
    private static String oracleDbName = "DBSTORE";
    private static String oracleUserName = "HDBCOOLER";
    private static String oraclePassword = "DBCOOLER240";
    private static String oracleConnUrl;

    private static boolean bReady = false;
    private static boolean bSnmpReady = false;

    private static ZYTScheduler scheduler;

    private static String requiredSignalsForSiteDetails = "957,958,959,960,961,962,977,980,983,985,986,987,989,990,991,992,993,994,1011,1014,1015";

    private static String requiredSignalsForHisData = "957,958,959,969,977,980,983,985,986";

    private static String requiredTypeIdsForAlarm = "0,6,8,11,14,15,19";

    private static int maxHisRecordsAmount = 10000;

    private static int maxHisDataExportCacheLine = 1000;

    public static int getMaxHisDataExportCacheLine() {
        return maxHisDataExportCacheLine;
    }

    public static int getMaxHisRecordsAmount() {
        return maxHisRecordsAmount;
    }

    public static String getRequiredSignalsForSiteDetails() {
        return requiredSignalsForSiteDetails;
    }

    public static String getRequiredSignalsForHisData() {
        return requiredSignalsForHisData;
    }

    public static String getRequiredTypeIdsForAlarm() {
        return requiredTypeIdsForAlarm;
    }

    public static int getSiteOnlineInternalInMinutes() {
        return siteOnlineInternalInMinutes;
    }

    public static String getStatsFilesFolder() {
        return statsFilesFolder;
    }

    public static int getDailyStatsFilesCount() {
        return dailyStatsFilesCount;
    }

    public static int getMonthlyStatsFilesCount() {
        return monthlyStatsFilesCount;
    }

    public static String getOracleIp() {
        return oracleIp;
    }

    public static int getOraclePort() {
        return oraclePort;
    }

    public static String getOracleDbName() {
        return oracleDbName;
    }

    public static String getOracleUserName() {
        return oracleUserName;
    }

    public static String getOraclePassword() {
        return oraclePassword;
    }

    public static String getOracleConnUrl() {
        return oracleConnUrl;
    }

    public synchronized void init() throws ServletException {
        if (bReady) {
            return;
        }

        super.init();

        //conf file in user.home
        File f = new File(System.getProperty("user.home") + File.separator + "imcws.properties");
        logger.debug("try to find cofiguration file: " + f.getAbsolutePath());
        Properties prop = new Properties();
        InputStream in = null;
        boolean bErrorOccured = false;
        boolean bLoadFromFile = false;
        if (f.exists()) {
            try {
                in = new FileInputStream(f);
                prop.load(in);
                bLoadFromFile = true;
            } catch (FileNotFoundException e) {
                bErrorOccured = true;
            } catch (IOException e) {
                bErrorOccured = true;
            } finally {
                Util.safeClose(in);
            }
        }
        if (bErrorOccured) {
            bErrorOccured = false;
            logger.error("failed to load parameters from configuration file; using internal parameters instead.");
        }

        dbHost = bLoadFromFile ? (String)prop.get("dbHost") : this.getInitParameter("dbHost");
        try {
            dbPort = Integer.parseInt(bLoadFromFile ? (String)prop.get("dbPort") : this.getInitParameter("dbPort"));
        } catch (NumberFormatException e) {
            throw new ServletException("invalid database port");
        }
        dbName = bLoadFromFile ? (String)prop.get("dbName") : this.getInitParameter("dbName");
        userName = bLoadFromFile ? (String)prop.get("userName") : this.getInitParameter("userName");
        password = bLoadFromFile ? (String)prop.get("password") : this.getInitParameter("password");
        try {
            siteOnlineInternalInMinutes = Integer.parseInt(bLoadFromFile ? (String)prop.get("siteOnlineInternalInMinutes")
                    : this.getInitParameter("siteOnlineInternalInMinutes"));
        } catch (NumberFormatException e) {
        }
        try {
            connPoolCount = Integer.parseInt(bLoadFromFile ? (String)prop.get("connPoolCount")
                    : this.getInitParameter("connPoolCount"));
        } catch (NumberFormatException e) {
        }

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("failed to load jdbc driver");
        }
        connURL = "jdbc:sqlserver://" + dbHost + ":" + dbPort + ";databaseName=" + dbName + ";user=" + userName + ";password=" + password;

        try {
            connPool = new ZYTWSConnPool(connPoolCount, connURL);
        } catch (SQLException e) {
            throw new ServletException("failed to instantiate conn pool");
        }

        try {
            snmpVer = Integer.parseInt(bLoadFromFile ? (String)prop.get("snmpVer") : this.getInitParameter("snmpVer"));
        } catch (NumberFormatException e) {
        }
        snmpCommunity = bLoadFromFile ? (String)prop.get("snmpCommunity") : this.getInitParameter("snmpCommunity");
        snmpTransport = bLoadFromFile ? (String)prop.get("snmpTransport") : this.getInitParameter("snmpTransport");
        snmpHost = bLoadFromFile ? (String)prop.get("snmpHost") : this.getInitParameter("snmpHost");
        try {
            snmpPort = Integer.parseInt(bLoadFromFile ? (String)prop.get("snmpPort") : this.getInitParameter("snmpPort"));
        } catch (NumberFormatException e) {
        }
        try {
            snmpTimeout = Integer.parseInt(bLoadFromFile ? (String)prop.get("snmpTimeout") : this.getInitParameter("snmpTimeout"));
        } catch (NumberFormatException e) {
        }
        try {
            snmpRetries = Integer.parseInt(bLoadFromFile ? (String)prop.get("snmpRetries") : this.getInitParameter("snmpRetries"));
        } catch (NumberFormatException e) {
        }

        try {
            snmp = new Snmp( snmpTransport.equalsIgnoreCase("udp") ? new DefaultUdpTransportMapping() : new DefaultTcpTransportMapping());
        } catch (IOException e) {
            bErrorOccured = true;
            logger.error("failed to instantiate snmp transport object.");
        }

        statsFilesFolder = bLoadFromFile ? (String)prop.get("statsFilesFolder") : this.getInitParameter("statsFilesFolder");
        if (!statsFilesFolder.endsWith(File.separator)) {
            statsFilesFolder += File.separator;
        }
        File exportingDir = new File(statsFilesFolder);  //added on 2015.10.16
        if (exportingDir.exists()) {
            if (!exportingDir.isDirectory()) {
                logger.error("the specified folder used to export statistics files is not a directory. Use '.' as the target folder.");
                statsFilesFolder = "." + File.separator;
            }
        } else {
            boolean bRst = exportingDir.mkdir();
            if (!bRst) {
                logger.error("failed to create the exporting folder. Use '.' as the target folder.");
                statsFilesFolder = "." + File.separator;
            }
        }
        try {
            dailyStatsFilesCount = Integer.parseInt(bLoadFromFile ? (String)prop.get("dailyStatsFilesCount") : this.getInitParameter("dailyStatsFilesCount"));
        } catch (NumberFormatException e) {
        }
        try {
            monthlyStatsFilesCount = Integer.parseInt(bLoadFromFile ? (String)prop.get("monthlyStatsFilesCount") : this.getInitParameter("monthlyStatsFilesCount"));
        } catch (NumberFormatException e) {
        }

        oracleIp = bLoadFromFile ? (String)prop.get("oracleIp") : this.getInitParameter("oracleIp");
        try {
            oraclePort = Integer.parseInt(bLoadFromFile ? (String)prop.get("oraclePort") : this.getInitParameter("oraclePort"));
        } catch (NumberFormatException e) {
        }
        oracleDbName = bLoadFromFile ? (String)prop.get("oracleDbName") : this.getInitParameter("oracleDbName");
        oracleUserName = bLoadFromFile ? (String)prop.get("oracleUserName") : this.getInitParameter("oracleUserName");
        oraclePassword = bLoadFromFile ? (String)prop.get("oraclePassword") : this.getInitParameter("oraclePassword");
        //jdbc:oracle:<drivertype>:<user>/<password>@<database> => jdbc:oracle:thin:scott/tiger@myhost:1521:orcl
        oracleConnUrl = "jdbc:oracle:thin:" + oracleUserName + "/" + oraclePassword + "@" + oracleIp + ":" + oraclePort + ":" + oracleDbName;
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("failed to load jdbc driver");
        }


        if (!bErrorOccured) {
            target = new CommunityTarget();
            target.setCommunity(new OctetString(snmpCommunity));
            target.setVersion(snmpVer);
            target.setAddress(snmpTransport.equalsIgnoreCase("udp") ? new UdpAddress(snmpHost + "/" + snmpPort) : new TcpAddress(snmpHost + "/" + snmpPort));
            target.setTimeout(snmpTimeout);
            target.setRetries(snmpRetries);
            bSnmpReady = true;
        }

        try {
            scheduler = new ZYTScheduler();
        } catch (SchedulerException e) {
            logger.error("failed to instantiate ZYTScheduler");
        }

        String requiredSigsForSiteDetails = bLoadFromFile ? (String)prop.get("requiredSignalsForSiteDetails") : this.getInitParameter("requiredSignalsForSiteDetails");
        if (requiredSigsForSiteDetails != null && requiredSigsForSiteDetails.trim().length() > 0) {
            requiredSignalsForSiteDetails = requiredSigsForSiteDetails;
        }

        String requiredSigsForHD = bLoadFromFile ? (String)prop.get("requiredSignalsForHisData") : this.getInitParameter("requiredSignalsForHisData");
        if (requiredSigsForHD != null && requiredSigsForHD.trim().length() > 0) {
            requiredSignalsForHisData = requiredSigsForHD;
        }

        String requiredTIForAlarm = bLoadFromFile ? (String)prop.get("requiredTypeIdsForAlarm") : this.getInitParameter("requiredTypeIdsForAlarm");
        if (requiredTIForAlarm != null && requiredTIForAlarm.trim().length() > 0) {
            requiredTypeIdsForAlarm = requiredTIForAlarm;
        }

        try {
            maxHisRecordsAmount = Integer.parseInt(bLoadFromFile ? (String)prop.get("maxhisalarmsexportamount") : this.getInitParameter("maxhisalarmsexportamount"));
        } catch (NumberFormatException e) {
        }

        try {
            maxHisDataExportCacheLine = Integer.parseInt(bLoadFromFile ? (String)prop.get("maxhisdataexportcacheline") : this.getInitParameter("maxhisdataexportcacheline"));
        } catch (NumberFormatException e) {
        }

        bReady = true;
    }

    public synchronized void destroy() {
        if (scheduler != null) {
            scheduler.shutdown();
        }
        if (connPool != null) {
            connPool.close();
        }
        try {
            Driver driver = DriverManager.getDriver(connURL);
            logger.debug("try to deregister driver " + driver);
            DriverManager.deregisterDriver(driver);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (snmp != null) {
            try {
                snmp.close();
            } catch (IOException e) {
            }
        }
        super.destroy();
    }

    public static ZYTWSConnection getConnection() {
        return connPool.getConn();
    }

    public static void releaseConnection(ZYTWSConnection conn) {
        connPool.releaseConn(conn);
    }

    public static boolean isbReady() {
        return bReady;
    }

    public static boolean isbSnmpReady() {
        return bSnmpReady;
    }

    public static PDU setOidValue(String oid, String value, int expression) {
        PDU pdu = createSetPdu(oid, value, expression);
        if (pdu == null) {
            return null;
        }
        ResponseEvent responseEvent = null;
        try {
            responseEvent = snmp.send(pdu, target);
        } catch (IOException e) {
            logger.error("failed to send snmp set request to peer");
        }
        return responseEvent == null ? null : responseEvent.getResponse();
    }

    private static PDU createSetPdu(String oid, String value, int expression) {
        PDU pdu = new PDU();
        pdu.setType(PDU.SET);
        try {
            int intValue = Integer.parseInt(value);
            pdu.add(new VariableBinding(new OID(oid), new Integer32(expression == 0 ? intValue : (intValue * expression))));
        } catch (NumberFormatException ne) {
            return null;
        }
        return pdu;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }
}
