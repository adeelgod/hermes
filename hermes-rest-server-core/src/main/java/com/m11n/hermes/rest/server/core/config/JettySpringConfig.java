package com.m11n.hermes.rest.server.core.config;

import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextListener;
import java.util.EventListener;

public class JettySpringConfig {
    private static final Logger logger = LoggerFactory.getLogger(JettySpringConfig.class);

    public static final String LISTENER_CONTEXT_LOADER_CLASS = "org.springframework.web.context.ContextLoaderListener";
    public static final String LISTENER_REQUEST_CONTEXT_CLASS = "org.springframework.web.context.request.RequestContextListener";
    public static final String PARAM_CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
    //public static final String PARAM_DISPATCH_OPTIONS_REQUEST = "dispatchOptionsRequest";

    @SuppressWarnings("unchecked")
    public static void addListener(ServletContextHandler context) throws Exception {
        Class<? extends ServletContextListener> contextLoaderListener = (Class<? extends ServletContextListener>) Class.forName(LISTENER_CONTEXT_LOADER_CLASS);
        Class<? extends ServletContextListener> requestContextListener = (Class<? extends ServletContextListener>) Class.forName(LISTENER_REQUEST_CONTEXT_CLASS);

        //ListenerHolder listenerHolder = new ListenerHolder(context);

        boolean contextListerFound = false;
        boolean requestListerFound = false;

        for(EventListener info : context.getEventListeners()) {
            if(info.getClass().equals(contextLoaderListener)) {
                logger.warn("Spring context loader listener already added.");
                contextListerFound = true;
                break;
            }
        }

        for(EventListener info : context.getEventListeners()) {
            if(info.getClass().equals(requestContextListener)) {
                logger.warn("Spring request context listener already added.");
                requestListerFound = true;
                break;
            }
        }

        if(!contextListerFound) {
            logger.info("Adding Spring context loader listener");
            context.setInitParameter(PARAM_CONTEXT_CONFIG_LOCATION, "classpath:META-INF/spring/applicationContext-hermes.xml");
            context.addEventListener(contextLoaderListener.newInstance());
        }

        if(!requestListerFound) {
            logger.info("Adding Spring request context listener");
            context.addEventListener(requestContextListener.newInstance());
        }
    }
}
