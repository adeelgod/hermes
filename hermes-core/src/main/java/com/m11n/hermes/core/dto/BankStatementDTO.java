package com.m11n.hermes.core.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BankStatementDTO {
    private static final String BANK_FIDOR = "fidor";
    private static final String BANK_HYPOVEIREINSBANK = "hypovereinsbank";

    private String orderId;
    private String invoiceId;
    private String bank;
    private String shop;
    private String type;
    private String orderStatus;
    private String id;
    private BigDecimal amountOrder;
    private BigDecimal amount;
    private String transferDate;
    private String orderDate;
    private String ebayName;
    private String firstName;
    private String lastName;
    private String company;
    private String customerId;
    private String text;
    private BigDecimal matching;
    private String status;

    private String orderIcon;
    private String action1;
    private String action2;

    @JsonProperty("bankIcon")
    public String getBankIcon() {
        return String.format("30_30_%s.bmp", bank);
    }
}
