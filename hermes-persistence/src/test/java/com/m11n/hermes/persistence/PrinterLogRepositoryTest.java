package com.m11n.hermes.persistence;

import com.m11n.hermes.core.model.PrinterLog;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Date;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PrinterLogRepositoryTest {
    private static final Logger logger = LoggerFactory.getLogger(PrinterLogRepositoryTest.class);

    @Inject
    private PrinterLogRepository printerLogRepository;

    @Test
    public void testSave() {
        PrinterLog log = new PrinterLog();
        log.setCharge("1");
        log.setInvoiceId("lkjasdfök");
        log.setOrderId("kjöasdfkj");
        log.setProcessedAt(new Date());
        log.setSelected(Boolean.TRUE);
        log.setShippingId("lkjasdföljk");
        log.setStatus("");

        log = printerLogRepository.save(log);

        logger.debug("*********************************** ENTITY ID: {}", log.getUid());
    }
}
