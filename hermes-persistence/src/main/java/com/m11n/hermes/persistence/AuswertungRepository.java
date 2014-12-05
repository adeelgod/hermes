package com.m11n.hermes.persistence;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class AuswertungRepository extends AbstractAuswertungRepository {
    public List<Map<String, Object>> query(String sqlStatement, Map<String, Object> parameters, RowMapper<Map<String, Object>> mapper) {
        return jdbcTemplate.query(sqlStatement, parameters, mapper);
    }

    public int update(String sqlStatement, Map<String, Object> parameters) {
        return jdbcTemplate.update(sqlStatement, parameters);
    }

    public String findShippingIdByOrderId(String orderId) {
        String sql = "select shipping_id from Auswertung.mage_custom_order where Bestellung = " + orderId;
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Collections.<String, Object>emptyMap());
        if(result.next()) {
            return result.getString(1);
        } else {
            return null;
        }
    }
}
