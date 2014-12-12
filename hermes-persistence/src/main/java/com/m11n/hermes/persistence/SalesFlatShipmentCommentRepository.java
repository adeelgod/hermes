package com.m11n.hermes.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class SalesFlatShipmentCommentRepository extends AbstractLCarbRepository {
    private static final Logger logger = LoggerFactory.getLogger(SalesFlatShipmentCommentRepository.class);

    public List<String> findRawStatus(String shippingId) {
        String sql = "Select comment from mage_sales_flat_shipment_comment where parent_id = " + shippingId + "  order by created_at limit 3";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Collections.<String, Object>emptyMap());
        List<String> ids = new ArrayList<>();
        while(result.next()) {
            ids.add(result.getString(1));
        }

        return ids;
    }
}
