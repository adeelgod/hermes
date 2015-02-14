package com.m11n.hermes.rest.server.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        Enumeration<String> headerNames = req.getHeaderNames();

        while(headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();

            logger.info("========> Header (req): {} = {}", name, req.getHeader(name));
        }

        Collection<String> names = res.getHeaderNames();

        for(String name : names) {
            logger.info("<======== Header (res): {} = {}", name, res.getHeader(name));
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
