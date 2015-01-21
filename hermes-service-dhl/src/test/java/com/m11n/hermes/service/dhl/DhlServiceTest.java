package com.m11n.hermes.service.dhl;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
@ActiveProfiles("debug")
public class DhlServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(DhlServiceTest.class);


    //private DefaultDhlService defaultService = new DefaultDhlService("danielschaaf", "", "dhl_entwicklerportal", "Dhl_123!", DhlService.MODE.SANDBOX);
    @Inject
    private JerryDhlService jerryService;

    @Test
    public void testTrackingCode() {
        //defaultService.getTrackingStatus("00340433836959504730"); // production
        //defaultService.getTrackingStatus("00340433836393297502"); // sandbox
        jerryService.getTrackingStatus("00340433836393297502"); // sandbox
    }

    @Test
    public void testCheckTracking() throws Exception {
        jerryService.checkTracking();

        int i = 0;

        while(jerryService.trackingCheckStatus() && i<3) {
            Thread.sleep(1000);
            logger.debug("#### WAITING FOR CHECKS TO FINISH...");
            i++;
        }

        try {
            jerryService.cancelTracking();
        } catch (Exception e) {
            // ignore
        }
    }
}
