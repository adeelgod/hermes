package com.m11n.hermes.service.magento;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class MagentoServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MagentoServiceTest.class);

    private Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    @Value("${hermes.magento.api.username}")
    private String username;

    @Value("${hermes.magento.api.password}")
    private String password;

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
