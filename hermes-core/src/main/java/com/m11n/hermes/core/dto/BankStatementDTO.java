package com.m11n.hermes.core.dto;

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
    private String orderId;
    private String invoiceId;
    private String bank;
    private String bankIcon;
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

    public void setBank(String bank) {
        this.bank = bank;
        this.bankIcon = String.format("30_30_%s.bmp", bank);
    }
}
