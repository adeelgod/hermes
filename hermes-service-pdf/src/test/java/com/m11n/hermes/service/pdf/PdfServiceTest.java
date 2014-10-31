package com.m11n.hermes.service.pdf;

import com.m11n.hermes.core.service.PdfService;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
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
    private static final Logger logger = LoggerFactory.getLogger(ExperimentsTest.class);

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
    public void testStatus() {
        String printerName = "Officejet_4630";
        //String printerName = "PDF";
        logger.info("++++++++++++++++++++++++++++ STATUS: {}", pdfService.status(printerName));
    }

    @Test
    public void testListPrinters() {
        // NOTE: see details here https://www.darklaunch.com/2011/02/26/ubuntu-print-to-pdf-install-a-pdf-printer

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        logger.info("Number of print services: " + printServices.length);

        for (PrintService printer : printServices) {
            logger.info("Printer: " + printer.getName());
        }

    }

    @Test
    public void testExtract() throws Exception {
        producer.sendBody("vm:extract", new FileInputStream("src/test/resources/invoice.pdf"));
    }

    @Test
    public void testPrint() throws Exception {
        //String printerName = "Officejet_4630";
        String printerName = "PDF";
        pdfService.print("src/test/resources/invoice-1.pdf", "1-1", printerName, "landscape", "", 1);
    }
}
