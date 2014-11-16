package com.m11n.hermes.persistence;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class IntrashipDocumentRepository extends AbstractLCarbRepository {
    public String findDocumentUrl(String orderId) {
        SqlRowSet result = jdbcTemplate.queryForRowSet("select document_url from mage_intraship_document where order_id = :orderId", Collections.singletonMap("orderId", orderId));
        return result.getString(1);
    }
}
