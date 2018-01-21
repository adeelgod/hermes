package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.PostalStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Types;

@Repository
public class SubFinanceRepository extends AbstractFairSheaRepository {
    private static final Logger logger = LoggerFactory.getLogger(SubFinanceRepository.class);

    public void insertPostalStamp(final PostalStamp postalStamp) {
        String sql = "INSERT INTO mage_postal_stamp(stamp_id, value, filename, generated, used, updated) SELECT MAX(stamp_id)  + 1, :value, :filename, NOW(), '0', '0000-00-00 00:00:00' FROM mage_postal_stamp";
        jdbcTemplate.update(sql, new MapSqlParameterSource().addValue(
                "value",
                postalStamp.getValue(),
                Types.DOUBLE
        ).addValue(
                "filename",
                postalStamp.getFileName(),
                Types.VARCHAR
        ));
    }
}
