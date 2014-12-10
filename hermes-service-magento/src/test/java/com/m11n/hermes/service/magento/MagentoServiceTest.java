package com.m11n.hermes.service.magento;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class MagentoServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(MagentoServiceTest.class);

    @Inject
    private DebugMagentoService magentoService;

    @Test
    public void testCreateIntrashipLabel() throws Exception {
        List<Map<String, Object>> messages = magentoService.createIntrashipLabel("30000001");

        logger.info("+++++++++++++++++ MESSAGES: {}", messages);
    }
}
