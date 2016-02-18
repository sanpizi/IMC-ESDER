package com.zyt.ws;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/4.
 */
@WebServlet(name = "StatsFileListServlet")
public class StatsFileListServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(StatsFileListServlet.class);

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null || type.length() == 0) {
            type = "daily";
        }
        File dir = new File(BootstrapServlet.getStatsFilesFolder());
        File[] statsFiles = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });
        StatsFileList data = new StatsFileList(type);
        if (statsFiles != null && statsFiles.length > 0) {
            for (File f : statsFiles) {
                String fileName = f.getName();
                if (type.equalsIgnoreCase("monthly") && fileName.startsWith("StatisticsDataReport") && fileName.endsWith("xlsx")) {
                    data.getRecordList().add(new FileItem(f.getName()));
                } else if (type.equalsIgnoreCase("daily") && fileName.startsWith("HisDataDaily") && fileName.endsWith("xlsx")) {
                    data.getRecordList().add(new FileItem(f.getName()));
                }
            }
            data.done();
        }
        logger.debug("start transferring to JSON string");
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(data);
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

    static class StatsFileList {
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public void setTotalRecords(int totalRecords) {
            this.totalRecords = totalRecords;
        }

        public ArrayList<FileItem> getRecordList() {
            return recordList;
        }

        public void setRecordList(ArrayList<FileItem> recordList) {
            this.recordList = recordList;
        }

        private String type;
        private int totalRecords;
        private ArrayList<FileItem> recordList;

        public StatsFileList(String type) {
            this.type = type;
            recordList = new ArrayList<FileItem>();
        }

        public void done() {
            totalRecords = recordList.size();
        }
    }

    static class FileItem {
        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }

        private String reportName;

        public FileItem(String reportName) {
            this.reportName = reportName;
        }
    }
}
