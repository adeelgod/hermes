package com.m11n.hermes.rest.server.config;

import com.m11n.hermes.rest.server.jersey.HermesApplication;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;

public class JettyJerseyConfig {
    private static final Logger logger = LoggerFactory.getLogger(JettyJerseyConfig.class);

    public static final String SERVLET_NAME = "JerseyServlet";
    public static final String SERVLET_CLASS = "org.glassfish.jersey.servlet.ServletContainer";
    public static final String PARAM_APPLICATION = "javax.ws.rs.Application";
    public static final String COMSAT_JERSEY_SERVLET_CLASS = "co.paralleluniverse.fibers.jersey.ServletContainer";
    public static final String COMSAT_JETTY_CLASSLOADER = "co.paralleluniverse.comsat.jetty.QuasarWebAppClassLoader";

    @SuppressWarnings("unchecked")
    public static void addServlet(ServletContextHandler context, String mapping, boolean comsat) throws Exception {

        logger.info("=================================== Adding Jersey Servlet with context {} {}", mapping, HermesApplication.class.getName());

        JettySpringConfig.addListener(context);

        String servletClass = comsat ? COMSAT_JERSEY_SERVLET_CLASS : SERVLET_CLASS;

        final ServletHolder servletHolder = new ServletHolder(SERVLET_NAME, (Class<? extends Servlet>) Class.forName(servletClass));
        servletHolder.setDisplayName(SERVLET_NAME);
        servletHolder.setInitOrder(1);
        servletHolder.setAsyncSupported(true);
        servletHolder.setInitParameter(PARAM_APPLICATION, HermesApplication.class.getName());
        context.addServlet(servletHolder, mapping);
    }

    public static boolean checkAgentArg(String[] args, boolean comsatEnabled) {
        boolean hasAgentArg = false;
        if(comsatEnabled) {
            String agentArg = "";

            for(String arg : args) {
                hasAgentArg = arg.startsWith("-javaagent:");

                if(hasAgentArg) {
                    agentArg = arg;
                    break;
                }
            }

            if(hasAgentArg) {
                logger.info("Comsat agent defined: {}", agentArg);
            } else {
                logger.warn("Comsat agent not defined!");
            }
        }

        return hasAgentArg;
    }
}
