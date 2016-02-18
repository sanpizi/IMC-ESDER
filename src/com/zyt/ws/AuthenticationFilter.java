package com.zyt.ws;

import com.zyt.user.UserDetails;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/15.
 */
@WebFilter(filterName = "AuthenticationFilter")
public class AuthenticationFilter implements Filter {
    private static final Logger logger = LogManager.getLogger(AuthenticationFilter.class);

    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        if (!BootstrapServlet.isbReady()) {
            logger.error("the system seems not ready, please check the db settings & web.xml");
            ((HttpServletResponse)resp).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        if (req instanceof HttpServletRequest) {
            String servlet = ((HttpServletRequest)req).getServletPath();
            String method = ((HttpServletRequest)req).getMethod();
            Object userDetails = ((HttpServletRequest)req).getSession().getAttribute("username");
            logger.debug(String.format("request on %1$s with %2$s method", servlet, method));
            if (userDetails == null || !doAuthorizationCheck((HttpServletRequest)req, (UserDetails)userDetails)) {
                logger.error("authentication required or insufficient authorization");
                resp.setContentLength(0);
                ((HttpServletResponse)resp).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(req, resp);
    }

    public void init(FilterConfig config) throws ServletException {
    }

    private static final String[] AVAILABLE_SIGNALS = new String[]{"FCS Reset",
            "A/C 1 Control", "A/C 2 Control",
            "Fan Control", "Work Mode","Fan Speed Control"  //add the new configuration item 2016.01.19
    };

    /**
     1.Manually Control指的是WS Settings界面中FCS Reset、A/C 1 Control、A/C 2 Control、Fan Control、Work Mode、Fan Speed Control
     2.Parameter Modifying指的是WS Settings界面中除了Manually Control的几项外其余的项
     */
    private boolean doAuthorizationCheck(HttpServletRequest req, UserDetails userDetais) {
        if (userDetais.getClerkType() == 1) { //admin
            return true;
        }
        if (req.getServletPath().equalsIgnoreCase("/modifyAccount")) {  //the only user management ops; others can only modify his own pswd
            String username = req.getParameter("username");
            return username != null && username.equals(userDetais.getClerkName());
        }
        switch (userDetais.getClerkType()) {
            case 0: //engineer; no user management ops
                return true;
            case 2: //guest; only browsing allowed
                return req.getMethod().equalsIgnoreCase("GET") && !req.getServletPath().equalsIgnoreCase("/exportHisAlarms") && !req.getServletPath().equalsIgnoreCase("/statsFile");
            case 4: //operator; only parameters modifying not allowed
                if (!req.getServletPath().equalsIgnoreCase("/config") || req.getMethod().equalsIgnoreCase("GET")) {
                    return true;
                } else {
                    String signalName = req.getParameter("signalName");
                    for (int i = 0; i < AVAILABLE_SIGNALS.length; i++) {
                        if (signalName.equalsIgnoreCase(AVAILABLE_SIGNALS[i])) {
                            return true;
                        }
                    }
                    return false;
                }
            default:
                break;
        }
        return false;
    }
}
