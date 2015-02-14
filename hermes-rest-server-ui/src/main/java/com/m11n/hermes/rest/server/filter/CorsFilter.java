package com.m11n.hermes.rest.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

// NOTE: this is only for local development
public class CorsFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    private static final long ONE_YEAR = TimeUnit.DAYS.toSeconds(365);

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        // see here:
        // - https://github.com/spring-projects/spring-framework/blob/49d7bda72248b5a08fda3d42ed10d9e127396e6a/spring-websocket/src/main/java/org/springframework/web/socket/sockjs/support/AbstractSockJsService.java#L355-L383
        // - http://atmosphere-framework.2306103.n4.nabble.com/Is-it-possible-to-connect-jquery-atmosphere-js-with-remote-Atmosphere-server-td4650715.html

        HttpServletResponse response = (HttpServletResponse)res;

        // CORS "pre-flight" request
        //This will allow requests from all domains
        String origin = request.getHeader("origin");
        origin = ((origin == null) || origin.equals("null")) ? "*" : origin;

        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Credentials", "true");
        //response.setHeader("Access-Control-Allow-Headers", header);
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type,Authorization,Accept,X-Cache-Date,X-Atmosphere-Framework,X-Atmosphere-tracking-id,X-Atmosphere-Transport");
        response.addHeader("Access-Control-Expose-Headers", "X-Cache-Date");
        response.setHeader("Access-Control-Max-Age", String.valueOf(ONE_YEAR));

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
