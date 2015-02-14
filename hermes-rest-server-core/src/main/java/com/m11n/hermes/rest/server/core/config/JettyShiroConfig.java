package com.m11n.hermes.rest.server.core.config;

import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

public class JettyShiroConfig {
    private static final Logger logger = LoggerFactory.getLogger(JettyShiroConfig.class);

    public static final String FILTER_NAME = "shiroFilter";
    public static final String FILTER_CLASS = "org.springframework.web.filter.DelegatingFilterProxy";
    public static final String PARAM_TARGET_FILTER_LIFECYCLE = "targetFilterLifecycle";
    public static final String PARAM_STATIC_SECURITY_MANAGER_ENABLED = "staticSecurityManagerEnabled";

    @SuppressWarnings("unchecked")
    public static void addFilter(ServletContextHandler context, String mapping) throws Exception {

        logger.info("=================================== Adding Shiro security filter with context {}", mapping);

        JettySpringConfig.addListener(context);

        FilterHolder filter = new FilterHolder((Class<? extends Filter>) Class.forName(FILTER_CLASS));
        filter.setAsyncSupported(true);
        filter.setName(FILTER_NAME);
        filter.setDisplayName(FILTER_NAME);
        filter.setInitParameter(PARAM_TARGET_FILTER_LIFECYCLE, "true");
        filter.setInitParameter(PARAM_STATIC_SECURITY_MANAGER_ENABLED, "true");

        context.addFilter(filter, mapping, EnumSet.allOf(DispatcherType.class));
    }
}
