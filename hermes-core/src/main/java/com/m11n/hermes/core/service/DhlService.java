package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.DhlTrackingStatus;

public interface DhlService {

    public static enum MODE {
        SANDBOX, PRODUCTION
    }

    public DhlTrackingStatus getTrackingStatus(String code);
}
