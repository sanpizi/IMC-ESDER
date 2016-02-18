package com.zyt.ws;

import com.zyt.SqlStmts;
import com.zyt.Util;
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
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/15.
 */
@WebServlet(name = "ModifyAccountServlet")
public class ModifyAccountServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(ModifyAccountServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String pswd = request.getParameter("newPassword");
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
        PreparedStatement stmt = null;
        int affectedRowCount = 0;
        try {
            stmt = conn.getConn().prepareStatement(SqlStmts.MODIFY_USER_PSWD);
            stmt.setString(1, pswd.trim());
            stmt.setString(2, username);
            affectedRowCount = stmt.executeUpdate();
            Util.safeClose(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(stmt);
            BootstrapServlet.releaseConnection(conn);
        }

        SimpleResult mr = new SimpleResult((affectedRowCount > 0) ? 0 : 1, (affectedRowCount > 0) ? null : "");
        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(mr);
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
