package com.m11n.hermes.service.magento;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MagentoServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MagentoServiceTest.class);

    private Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    private String username = "print2";
    private String password = "edgtds45";

    @Before
    public void setUp() throws Exception {
        MagentoServiceLocator locator = new MagentoServiceLocator();
        magentoService = locator.getMage_Api_Model_Server_V2_HandlerPort();
    }

    @Test
    public void testLogin() throws Exception {
        logger.info("New Magento session. Logging in...");
        String sessionId = magentoService.login(username, password);
        logger.info("Session ID: {}", sessionId);
    }
}
