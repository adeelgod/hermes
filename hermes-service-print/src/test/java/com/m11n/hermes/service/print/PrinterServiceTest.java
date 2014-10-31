package com.m11n.hermes.service.print;

import com.m11n.hermes.core.service.PrinterService;
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

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PrinterServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(PrinterServiceTest.class);

    @Inject
    private PrinterService printerService;

    @Before
    public void setUp() {
    }

    @Test
    public void testStatus() {
        try {
            String printerName = "Officejet_4630";
            //String printerName = "PDF";
            logger.info("++++++++++++++++++++++++++++ STATUS: {}", printerService.status(printerName));
        } catch (Throwable t) {
            // ignore for CI server
        }
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
    public void testPrint() throws Exception {
        try {
            //String printerName = "Officejet_4630";
            String printerName = "PDF";

            String file = "src/test/resources/invoice-1.pdf";
            //String file = "/tmp/hermes-test.pdf";

            printerService.print(file, "1-1", printerName, "landscape", null, 2);
            printerService.print(file, printerName);
        } catch (Throwable t) {
            // ignore for CI server
        }
    }
}
