package com.m11n.hermes.persistence;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AuswertungRepository extends AbstractAuswertungRepository {
    public List<Map<String, Object>> query(String sqlStatement, Map<String, Object> parameters, RowMapper<Map<String, Object>> mapper) {
        return jdbcTemplate.query(sqlStatement, parameters, mapper);
    }

    public int update(String sqlStatement, Map<String, Object> parameters) {
        // TODO: check insert/update
        //return jdbcTemplate.update(statement, parameters);
        return 0;
    }
}
