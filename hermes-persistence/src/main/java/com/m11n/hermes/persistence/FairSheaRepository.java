package com.m11n.hermes.persistence;

import com.google.common.base.Joiner;
import com.m11n.hermes.core.model.PostalStamp;
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
public class FairSheaRepository extends AbstractFairSheaRepository {
    private static final Logger logger = LoggerFactory.getLogger(FairSheaRepository.class);

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
