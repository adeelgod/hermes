package com.m11n.hermes.core.model;

public enum MagentoOrderServiceAction {
    SET_STATUS_TO_COMPLETED("status"), GENERATE_INVOICE("invoice"), GENERATE_SHIPPING("shipping");

    private final String type;

    MagentoOrderServiceAction(String type) {
        this.type = type;
    }

    public String getValue() {
        return type;
    }
}
