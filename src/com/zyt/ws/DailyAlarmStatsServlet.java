package com.zyt.ws;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.zyt.alarm.DailyAlarmStats;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by Administrator on 2015/8/9.
 */
@WebServlet(name = "DailyAlarmStatsServlet")
public class DailyAlarmStatsServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(DailyAlarmStatsServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long startTime = -1, endTime = -1;
        try {
            startTime = Long.parseLong(request.getParameter("startTime"));
        } catch (Throwable t) {
        }
        try {
            endTime = Long.parseLong(request.getParameter("endTime"));
        } catch (Throwable t) {
            endTime = startTime + (30L * 24 * 3600 * 1000);
        }
        ZYTWSConnection conn = BootstrapServlet.getConnection();

        try {
            DailyAlarmStats dailyAlarmStats = new DailyAlarmStats(startTime, endTime, conn.getConn());
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(dailyAlarmStats);
            logger.debug("done of transferring to JSON string");

            response.setContentType("application/json;charset=utf-8");
            response.setContentLength(content.length());
            response.getOutputStream().write(content.getBytes("utf-8"));
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().flush();
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
