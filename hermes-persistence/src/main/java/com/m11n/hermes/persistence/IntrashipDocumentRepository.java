package com.m11n.hermes.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
public class IntrashipDocumentRepository extends AbstractLCarbRepository {
    private static final Logger logger = LoggerFactory.getLogger(IntrashipDocumentRepository.class);

    public String findFilePath(String orderId) {
        String sql = "SELECT file_path FROM mage_intraship_document WHERE file_path LIKE \"%" + orderId+ ".pdf\"";
        logger.info("****************************************** {}", sql);
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, Collections.<String, Object>emptyMap());
        if(result.next()) {
            return result.getString(1);
        } else {
            return null;
        }
    }
}
