package com.m11n.hermes.service.pdf;

import com.m11n.hermes.core.service.PdfService;
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
import java.io.FileInputStream;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PdfServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(PdfServiceTest.class);

    @Inject
    private PdfService pdfService;

    @Before
    public void setUp() {
    }

    @Test
    public void testExtractInvoice() throws Exception {
        String result = pdfService.value(new FileInputStream("src/test/resources/invoice.pdf"), 1, "Bestellnummer:");

        logger.info("==================================================== ORDER ID: {}", result);

        Assert.assertEquals("300009816", result);
    }

    @Test
    public void testExtractLabels() throws Exception {
        String result = pdfService.value(new FileInputStream("src/test/resources/labels.pdf"), 1, "Referenznr.");

        logger.info("==================================================== ORDER ID: {}", result);

        Assert.assertEquals("300009624", result);
    }
}
