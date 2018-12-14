package com.m11n.hermes.service.dhl.util;

/**
 * Created by Umairb3 on 7/4/2017.
 */
public enum DHLResponseAttribute {

    DATA_ELE("data"),
    ERROR_ATTR("error"),
    STATUS_ATTR("status"),
    SEPARATOR("<<<>>>"),
    STATUS_TIMESTAMP_ATTR("status-timestamp");


    private String val;

    private DHLResponseAttribute (final String val) {
        this.val = val;

    }

    public String getVal() {
        return this.val;
    }
}
