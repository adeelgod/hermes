package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.FinanceChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
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

    public void importFidor(Map<String, String> row) {
        String sql = "INSERT INTO fidor_raw(date, text, text2, value) VALUES (:date, :text, :text2, :value)";
        jdbcTemplate.update(sql, row);
    }
}
