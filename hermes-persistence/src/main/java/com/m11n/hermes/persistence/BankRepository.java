package com.m11n.hermes.persistence;

import com.m11n.hermes.core.dto.BankStatementDTO;
import com.m11n.hermes.core.model.BankMatchIcon;
import com.m11n.hermes.core.model.Form;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
public class BankRepository extends AbstractAuswertungRepository {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Inject
    private FormRepository formRepository;

    public List<BankMatchIcon> getAllBankMatchIconAndActions() {
        String sql = "select * from bank_match_icon";
        return jdbcTemplate.query(sql, Collections.emptyMap(), new BankMatchIconMapper());
    }

    public List<BankStatementDTO> getAllMatched() {
        Form form = formRepository.findByName("bank_list_matched_new");
        return jdbcTemplate.query(form.getSqlStatement(), Collections.emptyMap(), new BankStatementDTOMapper());
    }

    public Optional<BankMatchIcon> getBankMatchIconAndActions(String shop, String type, String status) {
        Map<String, Object> params = new HashMap<>();
        params.put("shop", shop);
        params.put("type", type);
        params.put("status", status);
        String sql = "select * from bank_match_icon where UPPER(shop)=UPPER(:shop) AND UPPER(:type) LIKE CONCAT('%',UPPER(typ),'%') AND UPPER(status)=UPPER(:status)";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, params);

        return result.next() ? Optional.of(getBankMatchIconFromResultSet(result)) : Optional.empty();
    }

    private static BankMatchIcon getBankMatchIconFromResultSet(SqlRowSet rowSet) {
        return new BankMatchIcon(
                rowSet.getString("shop"),
                rowSet.getString("typ"),
                rowSet.getString("status"),
                rowSet.getString("icon"),
                rowSet.getString("action1"),
                rowSet.getString("action2")
        );
    }

    public static class BankMatchIconMapper extends BaseRowMapper<BankMatchIcon> {
        @Override
        public BankMatchIcon mapRow(ResultSet resultSet, int i) throws SQLException {
            return new BankMatchIcon(
                    resultSet.getString("shop"),
                    resultSet.getString("typ"),
                    resultSet.getString("status"),
                    resultSet.getString("icon"),
                    resultSet.getString("action1"),
                    resultSet.getString("action2")
            );
        }
    }

    public static class BankStatementDTOMapper extends BaseRowMapper<BankStatementDTO> {
        @Override
        public BankStatementDTO mapRow(ResultSet resultSet, int i) throws SQLException {
            BankStatementDTO bs = new BankStatementDTO();
            bs.setOrderId(resultSet.getString("orderId"));
            bs.setInvoiceId(resultSet.getString("invoiceId"));
            bs.setBank(resultSet.getString("bank"));
            bs.setShop(resultSet.getString("shop"));
            bs.setType(resultSet.getString("typ"));
            bs.setOrderStatus(resultSet.getString("order_status"));
            bs.setId(resultSet.getString("id"));
            bs.setAmountOrder(new BigDecimal(resultSet.getString("amountOrder")));
            bs.setAmount(new BigDecimal(resultSet.getString("amount")));

            bs.setTransferDate(resultSet.getString("transferDate"));
            bs.setOrderDate(LocalDateTime.parse(resultSet.getString("orderDate"), dateTimeFormatter).format(dateFormatter));

            bs.setEbayName(resultSet.getString("ebayName"));
            bs.setFirstName(resultSet.getString("firstname"));
            bs.setLastName(resultSet.getString("lastname"));
            bs.setCompany(resultSet.getString("firma"));
            bs.setCustomerId(resultSet.getString("customerId"));
            bs.setText(resultSet.getString("text"));
            bs.setMatching(new BigDecimal(resultSet.getString("matching")));

            // Only new are in the current bank application view "hermes_form > bank_list_match_new"
            bs.setStatus("new");

            return bs;
        }
    }
}
