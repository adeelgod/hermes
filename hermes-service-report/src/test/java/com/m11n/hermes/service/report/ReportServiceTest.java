package com.m11n.hermes.service.report;

import com.m11n.hermes.core.service.ReportService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class ReportServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(ReportServiceTest.class);

    @Inject
    private ReportService reportService;

    @Before
    public void setUp() {
    }

    @Test
    public void testGenerate() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("test_parameter", "POPIPANZ");
        reportService.generate("test.jrxml", parameters, "pdf", "target/test.pdf");
    }
}
