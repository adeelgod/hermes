package com.m11n.hermes.core.model;

public enum Bank {
    FIDOR("fidor"), HYPOVEREINSBANK("hypovereinsbank");

    private final String type;

    Bank(String type) {
        this.type = type;
    }

    public String getValue() {
        return type;
    }

    public static Bank getBank(String type) {
        return type.equalsIgnoreCase("fidor") ? FIDOR : HYPOVEREINSBANK;
    }
}
