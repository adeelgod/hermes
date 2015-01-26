package com.m11n.hermes.persistence;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class AuswertungRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(AuswertungRepositoryTest.class);

    @Inject
    private AuswertungRepository auswertungRepository;

    @Test
    public void test() {
        auswertungRepository.findOrderByFilter("abcdefghijklmnopqrstuvwxyz", "%", true, true, true, "30000000", true);
        logger.debug("=========================================================");
        auswertungRepository.findOrderByFilter("abcdefghijklmnopqrstuvwxyz", "%", true, false, false, "30000000", true);
        logger.debug("=========================================================");
        auswertungRepository.findOrderByFilter("abcdefghijklmnopqrstuvwxyz", "%", false, true, false, "30000000", true);
        logger.debug("=========================================================");
        auswertungRepository.findOrderByFilter("abcdefghijklmnopqrstuvwxyz", "%", false, false, true, "30000000", true);
        logger.debug("=========================================================");
        auswertungRepository.findOrderByFilter("abcdefghijklmnopqrstuvwxyz", "mü%", false, false, false, "30000000", false);
        logger.debug("=========================================================");
    }

    @Test
    public void testDhlQueries() {
        logger.debug("## date: {}", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date()));
        Long countResult = auswertungRepository.countDhlStatus("00340433836323587185");
        logger.debug("## count: {}", countResult);
        countResult = auswertungRepository.countDhlStatus("00340433836959656156");
        logger.debug("## count: {}", countResult);
        auswertungRepository.deleteDhlStatus("00340433836323587185");
        countResult = auswertungRepository.countDhlStatus("00340433836323587185");
        logger.debug("## count: {}", countResult);
        auswertungRepository.createDhlStatus("00340433836323587185", new Date(), "TEST STATUS");
        auswertungRepository.updateDhlStatus("00340433836323587185", new Date(), "TEST STATUS UPDATE");
        countResult = auswertungRepository.countDhlStatus("00340433836323587185");
        logger.debug("## count: {}", countResult);

        List<String> codes = auswertungRepository.findPendingTrackingCodes();

        logger.debug("## codes: {}", codes.size());
    }

    //@Test
    public void testTimestampPrint() {
        auswertungRepository.timestampPrint("200000053");
    }
}
