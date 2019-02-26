package com.m11n.hermes.rest.server.conf;

import com.m11n.hermes.rest.server.conf.condition.IsNonProductionCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath:META-INF/spring/applicationContext-hermes-camel.xml")
@Conditional(IsNonProductionCondition.class)
@Slf4j
public class CamelNonProductionConfig {
}
