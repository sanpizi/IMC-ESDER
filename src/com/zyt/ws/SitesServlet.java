package com.zyt.ws;

import com.zyt.sites.Sites;
import com.zyt.ws.util.ZYTWSConnection;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/1.
 */
@WebServlet(name = "SitesServlet")
public class SitesServlet extends HttpServlet {  //for /sites
    private static final Logger logger = LogManager.getLogger(SitesServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String[] siteIds = null;
        String siteIdsParam = request.getParameter("areaId");
        logger.debug("siteIdsParam is: " + siteIdsParam + "!");
        if (siteIdsParam != null && siteIdsParam.trim().length() != 0) {
            siteIds = siteIdsParam.trim().split(",");
        }
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
        String status = request.getParameter("status");
        if (status != null && status.trim().length() == 0) {
            status = null;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            Sites sites = siteIds == null ? new Sites(conn.getConn(), start, amount, orderBy, direction, status)
                    : new Sites(conn.getConn(), start, amount, orderBy, direction, status, siteIds);
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(sites);
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
