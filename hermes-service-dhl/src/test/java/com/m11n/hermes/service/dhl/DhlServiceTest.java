package com.m11n.hermes.service.dhl;

import com.m11n.hermes.core.service.DhlService;
import org.junit.Test;

public class DhlServiceTest {

    private DefaultDhlService defaultService = new DefaultDhlService("danielschaaf", "", "dhl_entwicklerportal", "Dhl_123!", DhlService.MODE.SANDBOX);

    private JerryDhlService jerryService = new JerryDhlService();

    @Test
    public void testTrackingCode() {
        //defaultService.getTrackingStatus("00340433836959504730"); // production
        //defaultService.getTrackingStatus("00340433836393297502"); // sandbox
        jerryService.getTrackingStatus("00340433836393297502"); // sandbox
    }
}
