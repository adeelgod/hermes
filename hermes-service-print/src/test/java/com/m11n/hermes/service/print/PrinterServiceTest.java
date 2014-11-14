package com.m11n.hermes.service.print;

import com.m11n.hermes.core.service.PrinterService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class PrinterServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(PrinterServiceTest.class);

    @Inject
    private PrinterService printerService;

    //private String printerName = "Officejet_4630";
    private String printerName = "PDF";

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
    public void testPrintInvoice() throws Exception {
        print("../../../hermes-service-pdf/src/test/resources/invoice-1.pdf");
    }

    @Test
    public void testPrintLabel() throws Exception {
        PDDocument pdf = PDDocument.load("../../../hermes-service-pdf/src/test/resources/labels.pdf");

        print("../../../hermes-service-pdf/src/test/resources/labels.pdf");
        print("../../../hermes-service-pdf/src/test/resources/intraship-labels.pdf");
    }

    @Test
    public void testToPng() throws Exception {
        logger.info("+++++++++++++++++ FILE: {}", new File("./").getAbsolutePath());
        PDDocument pdfDoc = PDDocument.load("../../../hermes-service-pdf/src/test/resources/labels.pdf");
        List<PDPage> pages = pdfDoc.getDocumentCatalog().getAllPages();
        PDPage page = pages.get(0);
        BufferedImage image = page.convertToImage();
        ImageIO.write(image, "png", new File("/tmp/freak/test.png"));
        ImageIO.write(image, "jpg", new File("/tmp/freak/test.jpg"));
        ImageIO.write(image, "gif", new File("/tmp/freak/test.gif"));
    }

    private void print(String file) {
        try {
            printerService.print(file, printerName);
        } catch (Throwable t) {
            // ignore for CI server
        }
    }
}
