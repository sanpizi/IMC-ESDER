package com.zyt.ws;

import com.zyt.alarm.HisAlarms;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/5.
 */
@WebServlet(name = "ExportHisAlarmsServlet")
public class ExportHisAlarmsServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ExportHisAlarmsServlet.class);

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
        if (amount == -1 || amount > BootstrapServlet.getMaxHisRecordsAmount()) {
            amount = BootstrapServlet.getMaxHisRecordsAmount();
        }
        String orderBy = request.getParameter("order");
        String direction = request.getParameter("direction");
        String status = request.getParameter("status");
        if (status != null && status.trim().length() == 0) {
            status = null;
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
            zoneId = Integer.parseInt(request.getParameter("zoneId"));
        } catch (NumberFormatException e) {
        }
        String zoneName = request.getParameter("zoneName");
        String startTime = request.getParameter("startTime");
        if (startTime != null && startTime.trim().length() == 0) {
            startTime = null;
        }
        String endTime = request.getParameter("endTime");
        if (endTime != null && endTime.trim().length() == 0) {
            endTime = null;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            HisAlarms data = new HisAlarms(conn.getConn(), start, amount, orderBy, direction, status, zoneId, zoneName,
                    siteId, siteName, signalId, signalName, startTime, endTime);
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=HisAlarm-"
                    + String.format("%1$tY%1$tm%1$td", new Date()) + ".xlsx");
            data.export(response.getOutputStream());
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
