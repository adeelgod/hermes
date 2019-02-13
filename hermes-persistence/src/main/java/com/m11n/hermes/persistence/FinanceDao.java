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

    public void importFinanceData(FinanceChannel channel, List<String> row) {
        switch (channel) {
            case FIDOR: importFidor(row); return;
            case HYPOVEREINSBANK: importHypovereinsbank(row); return;
            case PAYPAL: importPayPal(row); return;
            default: throw new UnsupportedOperationException(String.format("Unsupported bank import: %s", channel));
        }
    }

    private void importHypovereinsbank(List<String> row) {
        if (validate(row, 8)) {
            String sql = "INSERT INTO `hypovereinsbank_raw` (`Kontonummer`, `Buchungsdatum`, `Valuta`, `Empfaenger_1`, `Empfaenger_2`, `Verwendungszweck`, `Betrag`, `Waehrung`)" +
                    " VALUES (:account, :date, :value, :recipient1, :recipient2, :reason, :amount, :currency)";
            Map<String, Object> parameters = new HashMap<String, Object>() {
                {
                    put("account", row.get(0));
                    put("date", row.get(1));
                    put("value", row.get(2));
                    put("recipient1", row.get(3));
                    put("recipient2", row.get(4));
                    put("reason", row.get(5));
                    put("amount", row.get(6));
                    put("currency", row.get(7));
                }
            };

            jdbcTemplate.update(sql, parameters);
        }
    }

    private void importPayPal(List<String> row) {
        if (validate(row, 69)) {
            String sql = "INSERT INTO `paypal_raw` (`code1`, `Transaktionscode`, `Rechnungsnummer`, `PayPal-Referenznummer`, " +
                    "`PayPal-Referenznummer_Typ`, `Transaktionsereigniscode`, `Transaktionseinleitungsdatum`, " +
                    "`Transaktionsabschlussdatum`, `Transaktion_Gutschrift_oder_Belastung`, `Bruttotransaktionsbetrag`, " +
                    "`Währung_der_Transaktion`, `Gebühr_Soll_oder_Haben`, `Gebührenbetrag`, `Währung_der_Gebühr`, " +
                    "`Transaktionsstatus`, `Versicherungsbetrag`, `Umsatzsteuerbetrag`, `Versandkosten`, `Transaktionsgegenstand`, " +
                    "`Transaktionshinweis`, `Kontokennung_des_Käufers`, `Adressenstatus_des_Käufers`, `Artikelbezeichnung`, " +
                    "`Artikelnummer`, `Name_Option_1`, `Wert_Option_1`, `Name_Option_2`, `Wert_Option_2`, `Auktions-Site`, " +
                    "`Käufer-ID`, `Auktionsende`, `Versandadresse_Zeile_1`, `Lieferadresse_Zeile_2`, `Versandadresse_Ort`, " +
                    "`Lieferadresse_Bundesland`, `Lieferadresse_PLZ`, `Versandadresse_Land`, `Versandmethode`, " +
                    "`Benutzerdefiniertes_Feld`, `Rechnungsadresse_Zeile_1`, `Rechnungsadresse_Zeile_2`, `Rechnungsadresse_Ort`, " +
                    "`Rechnungsadresse_Staat`, `Rechnungsadresse_PLZ`, `Rechnungsadresse_Land`, `Kundennummer`, `Vorname`, " +
                    "`Nachname`, `Firmenname`, `Kartentyp`, `Zahlungsquelle`, `Versandname`, `Autorisierungsstatus`, " +
                    "`Anspruch_auf_Verkäuferschutz`, `Zahlungsverfolgungs-ID`, `Filiale`, `Kasse`, `Gutscheine`, " +
                    "`Sonderangebote`, `Kundenkartennummer`, `Zahlungstyp`, `Alternative_Lieferadresse_Zeile_1`, " +
                    "`Alternative_Lieferadresse_Zeile_2`, `Alternative_Lieferadresse_Ort`, `Alternative_Lieferadresse_Bundesland`, " +
                    "`Alternative_Lieferadresse_Land`, `Alternative_Lieferadresse_PLZ`, `3PL-Referenznummer`, `Geschenkkartennummer`) " +
                    "VALUES(:1, :2, :3, :4, :5, :6, :7, :8, :9, :10, :11, :12, :13, :14, :15, :16, :17, :18, :19, :20, :21, " +
                    ":22, :23, :24, :25, :26, :27, :28, :29, :30, :31, :32, :33, :34, :35, :36, :37, :38, :39, :40, :41, " +
                    ":42, :43, :44, :45, :46, :47, :48, :49, :50, :51, :52, :53, :54, :55, :56, :57, :58, :59, :60, :61, " +
                    ":62, :63, :64, :65, :66, :67, :68, :69)";

            SqlParameterSource parameters = new MapSqlParameterSource();
            int number = 1;
            for (String val : row) {
                ((MapSqlParameterSource) parameters).addValue(String.valueOf(number), val);
                number++;
            }

            jdbcTemplate.update(sql, parameters);
        }
    }

    private void importFidor(List<String> row) {
        if (validate(row, 4)) {
            String sql = "INSERT INTO fidor_raw(`date`, `text`, `text2`, `value`) VALUES (:date, :text, :text2, :value)";
            Map<String, Object> parameters = new HashMap<String, Object>() {
                {
                    put("date", row.get(0));
                    put("text", row.get(1));
                    put("text2", row.get(2));
                    put("value", row.get(3));
                }
            };

            jdbcTemplate.update(sql, parameters);
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
