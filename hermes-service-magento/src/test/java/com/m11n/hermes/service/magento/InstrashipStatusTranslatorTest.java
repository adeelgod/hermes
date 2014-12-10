package com.m11n.hermes.service.magento;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;

import static org.junit.Assert.*;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class InstrashipStatusTranslatorTest {

    private static final Logger logger = LoggerFactory.getLogger(InstrashipStatusTranslatorTest.class);

    @Inject
    private IntrashipStatusTranslator intrashipStatusTranslator;

    private String testSuccessMessage = "<br />\nDHL Intraship::pdf::0::PDF creation was successful<br />\nIhre L-Carb Shop Sendung ist Unterwegs<br />\nDHL Intraship::create::0::ok<br />\n (unknown)";

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

    @Test
    public void testNormalizeMessage() throws Exception {
        List<String> messages = intrashipStatusTranslator.normalizeMessage(testSuccessMessage);

        assertEquals(4, messages.size());

        for(String message : messages) {
            logger.debug("+++++++++++++++++++++++ TRANSLATOR NORMALIZE: {}", message);
        }
    }

    @Test
    public void testCheckSuccess() throws Exception {
        List<String> messages = intrashipStatusTranslator.normalizeMessage(testSuccessMessage);

        boolean success = intrashipStatusTranslator.check(messages, "success");

        logger.debug("+++++++++++++++++++++++ TRANSLATOR CHECK SUCCESS: {}", success);

        assertTrue(success);
    }

    public void testStatuses(String file, String status) throws Exception {
        LineIterator iterator = IOUtils.lineIterator(InstrashipStatusTranslatorTest.class.getClassLoader().getResourceAsStream(file), "UTF-8");

        while(iterator.hasNext()) {
            String message = iterator.next();
            String testStatus = intrashipStatusTranslator.toStatus(message);
            logger.debug("+++++++++++++++++++++++ TRANSLATOR MESSAGE STATUS: {} = {} - {}", status, testStatus, message);
            assertEquals(status, testStatus);
        }
    }
}
