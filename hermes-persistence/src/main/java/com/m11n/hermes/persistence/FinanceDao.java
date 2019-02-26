package com.m11n.hermes.persistence;

import com.m11n.hermes.core.exception.BankStatementImportValidationException;
import com.m11n.hermes.core.model.FinanceChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class FinanceDao extends AbstractFinanceDao {

    public void updateBank(FinanceChannel bank, String shop, String orderId, String id) {
        String sql = String.format(
                "UPDATE %s SET reference='%s', reference_id='%s', status='%s' where id=%d",
                bank.getValue(),
                shop,
                orderId,
                "confirm",
                Integer.valueOf(id)
        );

        jdbcTemplate.update(sql, Collections.EMPTY_MAP);
    }

    public void importBankData(String insertStatement, int expectedColumns, List<String> row) {
        if (validate(row, expectedColumns)) {
            SqlParameterSource parameters = new MapSqlParameterSource();
            int number = 1;
            for (String val : row) {
                ((MapSqlParameterSource) parameters).addValue(String.valueOf(number), val);
                number++;
            }

            jdbcTemplate.update(insertStatement, parameters);
        }
    }

    private boolean validate(List<String> row, int expectedColumnsSize) {
        if (row.size() == expectedColumnsSize) {
            return true;
        }

        throw new BankStatementImportValidationException(String.format(
                "Incorrect number of column %d, expecting %d",
                row.size(),
                expectedColumnsSize)
        );
    }
}
