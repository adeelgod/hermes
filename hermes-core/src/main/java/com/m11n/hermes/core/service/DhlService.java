package com.m11n.hermes.core.service;

public interface DhlService {

    public static enum MODE {
        SANDBOX, PRODUCTION
    }

    public String getTrackingStatus(String code);
}
