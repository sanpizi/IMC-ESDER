package com.zyt.ws;

import com.zyt.config.Config;
import com.zyt.config.ConfigDatas;
import com.zyt.ws.util.ComplexResult;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/9/4.
 */
@WebServlet(name = "ConfigServlet")
public class ConfigServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ConfigServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!BootstrapServlet.isbSnmpReady()) {
            logger.error("the system seems not ready, please check the snmp configuration");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        String siteIdStr = request.getParameter("siteId");
        if (siteIdStr == null) {
            logger.error("invalid parameters; no devSn found");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String signalName = request.getParameter("signalName");
        String value = request.getParameter("newValue");
        if (signalName == null || signalName.trim().length() == 0 || Config.getSignalIndex(signalName) == -1 || value == null || value.trim().length() == 0) {
            logger.error("invalid parameters; invalid signalName or value");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            ComplexResult er = Config.doConfig(conn.getConn(), siteIdStr, signalName, value);
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(er);
            logger.debug("done of transferring to JSON string");

            response.setContentType("application/json;charset=utf-8");
            response.setContentLength(content.length());
            response.getOutputStream().write(content.getBytes("utf-8"));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().flush();
        } catch (Throwable t ) {
            t.printStackTrace();
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!BootstrapServlet.isbSnmpReady()) {
            logger.error("the system seems not ready, please check the snmp configuration");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        String siteIdStr = request.getParameter("siteId");
        if (siteIdStr == null) {
            logger.error("invalid parameters; no siteId found");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String signalName = request.getParameter("signalName");
        if (signalName != null && Config.getSignalIndex(signalName.trim()) == -1) {  //may be null on 2016.01.02
            logger.error("invalid parameters; invalid signalName");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            ConfigDatas data = Config.doQuery(conn.getConn(), siteIdStr, signalName);
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(data);
            logger.debug("done of transferring to JSON string");

            response.setContentType("application/json;charset=utf-8");
            response.setContentLength(content.length());
            response.getOutputStream().write(content.getBytes("utf-8"));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().flush();
        } catch (Throwable t ) {
            t.printStackTrace();
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }
    }
}
