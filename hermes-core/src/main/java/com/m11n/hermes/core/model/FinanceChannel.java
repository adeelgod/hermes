package com.m11n.hermes.core.model;

public enum FinanceChannel {
    FIDOR("fidor"), HYPOVEREINSBANK("hypovereinsbank"), PAYPAL("paypal");

    private final String type;

    FinanceChannel(String type) {
        this.type = type;
    }

    public String getValue() {
        return type;
    }

    public static FinanceChannel getBank(String type) {
        return type.equalsIgnoreCase("fidor") ? FIDOR : HYPOVEREINSBANK;
    }
}
