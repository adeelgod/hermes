package com.m11n.hermes.persistence;

import com.google.common.base.Joiner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

@Repository
public class SubAuswertungRepository extends AbstractAuswertungRepository {
    private static final Logger logger = LoggerFactory.getLogger(SubAuswertungRepository.class);

    public List<Map<String, Object>> query(String sqlStatement, Map<String, Object> parameters, RowMapper<Map<String, Object>> mapper) {
        if(!StringUtils.isEmpty(sqlStatement) && !StringUtils.isEmpty(sqlStatement.trim())) {
            return jdbcTemplate.query(sqlStatement, parameters, mapper);
        } else {
            logger.warn("Query statement empty!");
            return Collections.emptyList();
        }
    }

    public int update(String sqlStatement, Map<String, Object> parameters) {
        try {
            if (!StringUtils.isEmpty(sqlStatement) && !StringUtils.isEmpty(sqlStatement.trim())) {
                return jdbcTemplate.update(sqlStatement, parameters);
            } else {
                logger.warn("Query statement empty!");
                return 0;
            }
        } catch(Exception ex) {
            logger.error("ERROR OCCURRED :: " + ex);
            return 0;
        }
    }

    public List<String> findPendingTrackingCodes() {
        return jdbcTemplate.query("SELECT tracking_no FROM mage_custom_shipments WHERE last_status != 'zugestellt'", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString(1);
            }
        });
    }

    public List<String> findAllByQuery(final String sqlStatement) {
        if(!StringUtils.isEmpty(sqlStatement) && !StringUtils.isEmpty(sqlStatement.trim())) {
            return jdbcTemplate.query(sqlStatement, new RowMapper<String>() {
                @Override
                public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return rs.getString(1);
                }
            });
        } else {
            return null;
        }
    }

    public void timestampPrint(String orderId) {
        jdbcTemplate.update("UPDATE mage_custom_order SET gedruckt = CURRENT_TIMESTAMP WHERE Bestellung = :orderId", Collections.singletonMap("orderId", orderId));
    }

    public Long countDhlStatus(String code) {
        return jdbcTemplate.queryForObject("select count(tracking_no) from mage_custom_shipments_DHL_status where tracking_no = :code", Collections.singletonMap("code", code), Long.class);
    }

    public void deleteDhlStatus(String code) {
        jdbcTemplate.update("DELETE FROM mage_custom_shipments_DHL_status where tracking_no = :code", Collections.singletonMap("code", code));
    }

    public void updateOrderLastStatus(String code, String status) {
        Map<String, String> params = new HashMap<>();
        params.put("code", code);
        params.put("status", status);
        jdbcTemplate.update("UPDATE mage_custom_shipments SET last_status = :status, Daytime_last_status_change = NOW() WHERE tracking_no = :code", params);
    }

    public void createDhlStatus(String code, Date date, String status) {
        jdbcTemplate.update("INSERT  INTO mage_custom_shipments_DHL_status (tracking_no, Status_time, Status) VALUES ( :code, :date_status, :status )", new MapSqlParameterSource().addValue(
                "date_status",
                new java.sql.Date(date.getTime()),
                //new SimpleDateFormat("yyyy-MM-dd").format(date),
                Types.TIMESTAMP
        ).addValue(
                "code",
                code,
                Types.VARCHAR
        ).addValue(
                "status",
                status,
                Types.VARCHAR
        ));
    }

    /**
     * Insert query of DHL status: With extra column status, Status_time, status_xml, request_time
     * @param code
     * @param date
     * @param status
     * @param statusXML
     */
    public void createDhlStatus(String code, Date date, String status, String statusXML, Date requestTime) {
        jdbcTemplate.update("INSERT  INTO mage_custom_shipments_DHL_status (tracking_no, Status_time, Status, status_xml, request_time) VALUES ( :code, :date_status, :status ,:statusXml, :request_time)", new MapSqlParameterSource().addValue(
                "date_status",
                new java.sql.Date(date.getTime()),
                //new SimpleDateFormat("yyyy-MM-dd").format(date),
                Types.TIMESTAMP
        ).addValue(
                "code",
                code,
                Types.VARCHAR
        ).addValue(
                "status",
                status,
                Types.VARCHAR
        ).addValue(
                "statusXml",
                statusXML,
                Types.VARCHAR
        ).addValue(
                "request_time",
                new java.sql.Date(requestTime.getTime()),
                Types.TIMESTAMP
        ));
    }

    public void updateDhlStatus(String code, Date date, String status) {
        jdbcTemplate.update("UPDATE mage_custom_shipments_DHL_status SET Status_time = :date_status, Status = :status WHERE tracking_no = :code", new MapSqlParameterSource().addValue(
                "date_status",
                new java.sql.Date(date.getTime()),
                Types.TIMESTAMP
        ).addValue(
                "code",
                code,
                Types.VARCHAR
        ).addValue(
                "status",
                status,
                Types.VARCHAR
        ));
    }

    /**
     * update query of DHL Status: with status, Status_Time and Status_xml
     * @param code
     * @param date
     * @param status
     * @param statusXML
     */
    public void updateDhlStatus(String code, Date date, String status, String statusXML, Date requestTime) {
        jdbcTemplate.update("UPDATE mage_custom_shipments_DHL_status SET Status_time = :date_status, Status = :status, status_xml=:statusXml, request_time=:requestTime WHERE tracking_no = :code", new MapSqlParameterSource().addValue(
                "date_status",
                new java.sql.Date(date.getTime()),
                Types.TIMESTAMP
        ).addValue(
                "code",
                code,
                Types.VARCHAR
        ).addValue(
                "status",
                status,
                Types.VARCHAR
        ).addValue(
                "statusXml",
                statusXML,
                Types.VARCHAR
        ).addValue(
                "requestTime",
                new java.sql.Date(requestTime.getTime()),
                Types.TIMESTAMP
        ));
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

    public void updateOrderPaymentId(String orderId, String bankStatementId) {
        Map<String, Object> params = new HashMap<>();
        params.put("bankStatementId", bankStatementId);
        params.put("orderId", orderId);

        jdbcTemplate.update("UPDATE mage_custom_order SET payment_id = :bankStatementId WHERE Bestellung = :orderId", params);
    }

    @Deprecated
    public List<Map<String, Object>> findBankStatementOrderByMatch(String uuid, int lookupPeriod) {
        try {
            String sql = IOUtils.toString(SubAuswertungRepository.class.getClassLoader().getResourceAsStream("bank_statement_match.sql")).replaceAll(":uuid", "\"" + uuid + "\"").replaceAll(":lookup", lookupPeriod + "");

            //return jdbcTemplate.query(sql, Collections.<String, Object>singletonMap("uuid", uuid), new DefaultMapper());
            return query(sql, Collections.<String, Object>emptyMap(), new DefaultMapper());
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }

        return Collections.emptyList();
    }

    public List<Map<String, Object>> findBankStatementOrderByFilter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or) {
        try {
            String sql = IOUtils.toString(SubAuswertungRepository.class.getClassLoader().getResourceAsStream("bank_statement_filter.sql"));

            String join = or ? " OR " : " AND ";

            List<String> filters = new ArrayList<>();

            // NOTE: by default lastname will be filtered if nothing is selected
            if(!(amount || amountDiff || lastname) && (StringUtils.isEmpty(lastnameCriteria) || "%".equals(lastnameCriteria)) && StringUtils.isEmpty(orderId)) {
                lastname = true;
            }

            if(amount) {
                filters.add("a.GesamtPreis_der_Bestellung_Brutto = b.amount");
            }
            if(amountDiff) {
                filters.add("abs(a.GesamtPreis_der_Bestellung_Brutto - b.amount) < 1");
            }
            if(lastname) {
                filters.add("locate(\n" +
                        "  left(replace(replace(replace(replace(lower(a.Kunden_name), 'ä', ''), 'ö', ''), 'ü', ''), 'ß', ''), 4)," +
                        "  replace(replace(replace(replace(lower(b.description), 'ä', ''), 'ö', ''), 'ü', ''), 'ß', '')) >= 0");
            }
            if(!StringUtils.isEmpty(orderId)) {
                filters.add("a.Bestellung = " + orderId);
            }

            if(!StringUtils.isEmpty(lastnameCriteria)) {
                filters.add("a.Kunden_name LIKE '" + lastnameCriteria + "'");
            }

            sql = String.format(sql, Joiner.on(join).join(filters));

            return query(sql, Collections.<String, Object>singletonMap("uuid", uuid), new DefaultMapper());
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }

        return Collections.emptyList();
    }

    public void assignBankstatementOrders(String uuid, List<String> orderIds) {
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("orderIds", orderIds);
        // TODO: test this, column still missing
        jdbcTemplate.update("UPDATE mage_custom_order SET bank_statement_id = :uuid WHERE Bestellung IN (:orderIds)", params);
    }

    public void unassignBankstatementOrders(String uuid) {
        // TODO: test this, column still missing
        jdbcTemplate.update("UPDATE mage_custom_order SET bank_statement_id = NULL WHERE bank_statement_id = :uuid", Collections.singletonMap("uuid", uuid));
    }

    public static class DefaultMapper extends BaseRowMapper<Map<String, Object>> {
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
