package com.m11n.hermes.core.util;

/**
 * Created by Umairb3 on 7/2/2017.
 */
public enum DhlApiLanguage {

    DEUTSCHLAND("de"),
    ENGLISH("en");

    private String value;

    private DhlApiLanguage(final String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
