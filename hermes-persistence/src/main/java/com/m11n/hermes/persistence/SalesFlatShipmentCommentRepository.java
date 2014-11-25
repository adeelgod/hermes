package com.m11n.hermes.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class SalesFlatShipmentCommentRepository extends AbstractLCarbRepository {
    private static final Logger logger = LoggerFactory.getLogger(SalesFlatShipmentCommentRepository.class);

    public String findRawStatus(String shippingId, int pos) {
        String sql = "Select comment from mage_sales_flat_shipment_comment where parent_id = " + shippingId + "  order by created_at limit " + pos + "," + (pos+1);
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Collections.<String, Object>emptyMap());
        if(result.next()) {
            return result.getString(1);
        } else {
            return null;
        }
    }
}
