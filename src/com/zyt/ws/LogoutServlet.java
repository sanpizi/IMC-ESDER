package com.zyt.ws;

import com.zyt.ws.util.SimpleResult;
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
 * Created by Administrator on 2015/8/15.
 */
@WebServlet(name = "LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(LogoutServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getSession().removeAttribute("username");
        request.getSession().invalidate();

        SimpleResult lr = new SimpleResult(0, null);

        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(lr);
        logger.debug("done of transferring to JSON string");

        response.setContentType("application/json;charset=utf-8");
        response.setContentLength(content.length());
        response.getOutputStream().write(content.getBytes("utf-8"));
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
