package com.m11n.hermes.service.magento;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.FileInputStream;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class InstrashipStatusTranslatorTest {

    private static final Logger logger = LoggerFactory.getLogger(InstrashipStatusTranslatorTest.class);

    @Inject
    private IntrashipStatusTranslator intrashipStatusTranslator;

    @Test
    public void testSuccess() throws Exception {
        testStatuses("label_status_success.txt", "success");
    }

    @Test
    public void testError() throws Exception {
        testStatuses("label_status_error.txt", "error");
    }

    @Test
    public void testWarning() throws Exception {
        testStatuses("label_status_warning.txt", "warning");
    }

    @Test
    public void testRetry() throws Exception {
        testStatuses("label_status_retry.txt", "retry");
    }

    public void testStatuses(String file, String status) throws Exception {
        LineIterator iterator = IOUtils.lineIterator(InstrashipStatusTranslatorTest.class.getClassLoader().getResourceAsStream(file), "UTF-8");

        while(iterator.hasNext()) {
            String message = iterator.next();
            String testStatus = intrashipStatusTranslator.toStatus(message);
            logger.debug("Translating message to status: {} - {} = {}", message, status, testStatus);
            Assert.assertEquals(status, testStatus);
        }
    }
}
