package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.DhlTrackingStatus;

public interface DhlService {

    public static enum MODE {
        SANDBOX, PRODUCTION
    }

    DhlTrackingStatus getTrackingStatus(String code);

    boolean trackingCheckStatus();

    void checkTracking();

    void cancelTracking() throws Exception;
}
