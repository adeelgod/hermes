package com.m11n.hermes.persistence;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
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

    public List<Map<String, Object>> findOrdersByOrderId(String orderId) {
        String sql = "select Bestellung as orderId, GesamtPreis_der_Bestellung_Brutto as amount, Kundenummer as clientId, Kunden_vorname as firstname, Kunden_name as lastname, Kunden_ebay_name as ebayName, typ as type, Status as status from Auswertung.mage_custom_order where Bestellung = " + orderId;

        return jdbcTemplate.query(sql, Collections.<String, Object>emptyMap(), new DefaultMapper());
    }

    public static class DefaultMapper extends BaseRowMapper {
        @Override
        public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
            Map<String, Object> row = new HashMap<>();
            ResultSetMetaData metaData = resultSet.getMetaData();

            for(int j=1; j<=metaData.getColumnCount(); j++) {
                String name = getLabel(metaData, j);
                Object value = getValue(resultSet, j);

                row.put(name, value);
            }

            return row;
        }
    }
}
