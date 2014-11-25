package com.m11n.hermes.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class SalesFlatShipmentCommentRepository extends AbstractLCarbRepository {
    private static final Logger logger = LoggerFactory.getLogger(SalesFlatShipmentCommentRepository.class);

    // TODO: fix shipping ID! 2 different databases
    public String findRawStatus(String orderId) {
        String sql = "Select comment from mage_sales_flat_shipment_comment where parent_id = (select shipping_id from Auswertung.mage_custom_order where Bestellung = " + orderId + ")  order by created_at limit 0,3";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Collections.<String, Object>emptyMap());
        if(result.next()) {
            return result.getString(1);
        } else {
            return null;
        }
    }
}
