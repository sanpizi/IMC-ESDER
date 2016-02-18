package com.zyt.ws;

import com.zyt.area.Areas;
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
 * Created by Administrator on 2015/8/12.
 */
@WebServlet(name = "AreasServlet")
public class AreasServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(GlobalStatsServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ZYTWSConnection conn = BootstrapServlet.getConnection();
        try {
            Areas data = new Areas(conn.getConn());
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
