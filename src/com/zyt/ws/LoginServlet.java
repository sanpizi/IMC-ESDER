package com.zyt.ws;

import com.zyt.user.UserDetails;
import com.zyt.ws.util.ExSimpleResult;
import com.zyt.ws.util.SimpleResult;
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
 * Created by Administrator on 2015/8/15.
 */
@WebServlet(name = "LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(LoginServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!BootstrapServlet.isbReady()) {
            logger.error("the system seems not ready, please check the db settings in db & web.xml");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        String username = request.getParameter("username");
        String pswd = request.getParameter("password");
        if (username == null || username.trim().length() == 0
                || pswd == null || pswd.trim().length() == 0) {
            logger.error("invalid username or password");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (username.startsWith("\"") || username.startsWith("'")) {
            username = username.substring(1, username.length());
        }
        if (username.endsWith("\"") || username.endsWith("'")) {
            username = username.substring(0, username.length() - 1);
        }
        if (pswd.startsWith("\"") || pswd.startsWith("'")) {
            pswd = pswd.substring(1, pswd.length());
        }
        if (pswd.endsWith("\"") || pswd.endsWith("'")) {
            pswd = pswd.substring(0, pswd.length() - 1);
        }

        if (username.trim().length() == 0 || pswd.trim().length() == 0) {
            logger.error("invalid username or password");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        ZYTWSConnection conn = BootstrapServlet.getConnection();
        UserDetails userDetails = null;
        try {
            userDetails = new UserDetails(conn.getConn(), username);
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            BootstrapServlet.releaseConnection(conn);
        }

        SimpleResult lr = null;
        if (userDetails.getID() != -1 && userDetails.getPassword().equals(pswd)) {
            lr = new ExSimpleResult(userDetails.getClerkTypeStr(), 0, null);
            request.getSession(true).setAttribute("username", userDetails);
        } else {
            lr = new SimpleResult(1, "The user name or password is incorrect.");
        }

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
