package com.zyt.ws;

import com.zyt.alarm.RealTimeAlarms;
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
 * Created by Administrator on 2015/8/21.
 */
@WebServlet(name = "RealAlarmsServlet")
public class RealAlarmsServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(RealAlarmsServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int start = 1;
        try {
            start = Integer.parseInt(request.getParameter("start"));
        } catch (Throwable t) {
        }
        int amount = -1;
        try {
            amount = Integer.parseInt(request.getParameter("amount"));
        } catch (Throwable t) {
        }
        String orderBy = request.getParameter("order");
        String direction = request.getParameter("direction");
        String severity = request.getParameter("status");
        if (severity != null && severity.trim().length() == 0) {
            severity = null;
        }
        if (severity != null && (severity.startsWith("\"") || severity.startsWith("'"))) {
            severity = severity.substring(1);
            severity = severity.substring(0, severity.length() - 1);
        }
        int signalId = -1;
        try {
            signalId = Integer.parseInt(request.getParameter("signalId"));
        } catch (Throwable t) {
        }
        String signalName = request.getParameter("signalName");
        int siteId = -1;
        try {
            siteId = Integer.parseInt(request.getParameter("siteId"));
        } catch (NumberFormatException e) {
        }
        String siteName = request.getParameter("siteName");
        int zoneId = -1;
        try {
            zoneId = Integer.parseInt(request.getParameter("areaId"));
        } catch (NumberFormatException e) {
        }
        String zoneName = request.getParameter("areaName");

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            RealTimeAlarms data = new RealTimeAlarms(conn.getConn(), start, amount, orderBy, direction, severity,
                    zoneId, zoneName, siteId, siteName, signalId, signalName);
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(data);
            logger.debug("done of transferring to JSON string");

            response.setContentType("application/json;charset=utf-8");
            response.setContentLength(content.length());
            response.getOutputStream().write(content.getBytes("utf-8"));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().flush();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
