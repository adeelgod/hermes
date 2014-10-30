package com.m11n.hermes.rest.server.jersey;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HermesApplication extends ResourceConfig {
    private static final Logger logger = LoggerFactory.getLogger(HermesApplication.class);

    public HermesApplication() {
        setApplicationName("hermes")
        // see here: https://jersey.java.net/documentation/latest/deployment.html#deployment.autodiscovery.config
        .packages("com.m11n.hermes")
        // Spring
        .register(RequestContextFilter.class)
        //.register(CsrfProtectionFilter.class) // TODO: enable this in production
        .register(MultiPartFeature.class)
        // Now you can expect validation errors to be sent to the client.
        .property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true)
        // @ValidateOnExecution annotations on subclasses won't cause errors.
        .property(ServerProperties.BV_DISABLE_VALIDATE_ON_EXECUTABLE_OVERRIDE_CHECK, true)
        // Set entity-filtering scope via configuration.
        //.property(EntityFilteringFeature.ENTITY_FILTERING_SCOPE, new Annotation[] {ProjectDetailedView.Factory.get()})
        // Register the EntityFilteringFeature.
        //.register(EntityFilteringFeature.class)
        // Set entity-filtering scope via configuration.
        //.property(EntityFilteringFeature.ENTITY_FILTERING_SCOPE, new Annotation[] {SecurityAnnotations.rolesAllowed("manager")})
        // Register the SecurityEntityFilteringFeature.
        //.register(SecurityEntityFilteringFeature.class)
        //.register(RolesAllowedDynamicFeature.class)
        //.register(DeclarativeLinkingFeature.class)
        //.register(LoggingFilter.class)
        //.register(HluExceptionMapper.class)
        ;
    }
}
