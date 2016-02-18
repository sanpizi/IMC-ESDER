package com.zyt.ws;

import com.zyt.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Administrator on 2015/10/4.
 */
@WebServlet(name = "StatsFileServlet")
public class StatsFileServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(StatsFileServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        if (name == null || name.length() == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(BootstrapServlet.getStatsFilesFolder() + name);
        if (!file.exists() || !file.isFile()) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        boolean bErrorOccured = false;
        DataInputStream stream = null;
        byte[] content = null;
        try {
            stream = new DataInputStream(new FileInputStream(file));
            content = new byte[(int)file.length()];
            stream.readFully(content);
        } catch (IOException e) {
            bErrorOccured = true;
        } finally {
            Util.safeClose(stream);
        }

        if (bErrorOccured || content == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        response.getOutputStream().write(content);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
