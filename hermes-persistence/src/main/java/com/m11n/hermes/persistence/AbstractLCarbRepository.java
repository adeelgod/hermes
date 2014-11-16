package com.m11n.hermes.persistence;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import javax.inject.Named;

public abstract class AbstractLCarbRepository {
    @Inject
    @Named("jdbcTemplateLCarb")
    protected NamedParameterJdbcTemplate jdbcTemplate;
}
