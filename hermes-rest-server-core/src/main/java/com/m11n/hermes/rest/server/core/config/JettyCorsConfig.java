package com.m11n.hermes.rest.server.core.config;

import com.m11n.hermes.rest.server.core.filter.CorsFilter;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.EnumSet;

public class JettyCorsConfig {
    private static final Logger logger = LoggerFactory.getLogger(JettyCorsConfig.class);

    public static final String FILTER_NAME = "CorsFilter";

    @SuppressWarnings("unchecked")
    public static void addFilter(ServletContextHandler context, String mapping) throws Exception {

        logger.info("Adding CORS filter with context {}", mapping);

        FilterHolder filter = new FilterHolder((Class<? extends Filter>) Class.forName(CorsFilter.class.getName()));
        filter.setAsyncSupported(false);
        filter.setName(FILTER_NAME);
        filter.setDisplayName(FILTER_NAME);

        context.addFilter(filter, mapping, EnumSet.allOf(DispatcherType.class));
    }
}
