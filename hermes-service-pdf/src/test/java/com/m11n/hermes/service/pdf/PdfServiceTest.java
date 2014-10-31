package com.m11n.hermes.service.pdf;

import com.m11n.hermes.core.service.PdfService;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.FileInputStream;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PdfServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(PdfServiceTest.class);

    @Inject
    private PdfService pdfService;

    @Inject
    private ProducerTemplate producer;

    @EndpointInject(uri = "mock:result")
    protected MockEndpoint resultEndpoint;

    @Before
    public void setUp() {
    }

    @Test
    public void testSend() throws Exception {
        producer.sendBody("vm:extract", new FileInputStream("src/test/resources/invoice.pdf"));
    }

    @Test
    public void testExtract() throws Exception {
        String result = pdfService.value(new FileInputStream("src/test/resources/invoice.pdf"), 1, "Bestellnummer:");

        logger.info("==================================================== ORDER ID: {}", result);

        Assert.assertEquals("300009816", result);
    }
}
