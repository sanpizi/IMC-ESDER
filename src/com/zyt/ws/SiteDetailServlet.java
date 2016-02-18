package com.zyt.ws;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.zyt.sitedetail.SiteDetail;
import com.zyt.ws.util.ZYTWSConnection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * Created by Administrator on 2015/8/1.
 */
@WebServlet(name = "SiteDetailServlet")
public class SiteDetailServlet extends HttpServlet {  //for /site/*
    private static final Logger logger = LogManager.getLogger(SiteDetailServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int siteId = 0;
        String url = request.getRequestURI();
        try {
            siteId = Integer.parseInt(url.substring(url.lastIndexOf("/") + 1));
        } catch (Throwable e) {
            logger.error("invaid sites id");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            SiteDetail site = new SiteDetail(conn.getConn(), siteId);
            logger.debug("start transferring to JSON string");
            ObjectMapper mapper = new ObjectMapper();
            String content = mapper.writeValueAsString(site);
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
