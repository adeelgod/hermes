package com.m11n.hermes.rest.server.conf.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class IsProductionCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context,
                           AnnotatedTypeMetadata metadata) {
        Environment env = context.getEnvironment();
        String springActiveProfile = env.getProperty("spring.profiles.active");
        return springActiveProfile != null && "production".equals(springActiveProfile.replaceAll("[\"]+", ""));
    }
}
