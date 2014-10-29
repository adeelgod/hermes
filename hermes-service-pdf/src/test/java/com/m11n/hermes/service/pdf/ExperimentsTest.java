package com.m11n.hermes.service.pdf;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.FileDataSource;
import java.io.*;
import java.util.List;

public class ExperimentsTest {
    private static final Logger logger = LoggerFactory.getLogger(ExperimentsTest.class);

    @Test
    public void testExtractInvoice() throws Exception {
        extract("src/test/resources/invoice.pdf", "target/result-invoice.txt", 1, 1);
    }

    @Test
    public void testExtractLabels() throws Exception {
        extract("src/test/resources/labels.pdf", "target/result-labels.txt", 1, 1);
    }

    @Test
    public void testSplit() throws Exception {
        FileDataSource fd = new FileDataSource("src/test/resources/invoice.pdf");

        PDFParser parser = new PDFParser(fd.getInputStream());
        parser.parse();

        PDDocument doc = parser.getPDDocument();

        Splitter splitter = new Splitter();
        splitter.setStartPage(1);
        splitter.setEndPage(1);
        List<PDDocument> result = splitter.split(doc);

        int i = 0;

        for(PDDocument d : result) {
            i++;
            d.save("target/invoice-" + i + ".pdf");
        }
    }

    private void extract(String pdfFile, String resultFile, int from, int to) throws Exception {
        FileDataSource fd = new FileDataSource(pdfFile);

        PDFParser parser = new PDFParser(fd.getInputStream());
        parser.parse();

        PDDocument doc = parser.getPDDocument();

        //Writer writer = new StringWriter();
        Writer writer = new FileWriter(resultFile);

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(from);
        stripper.setEndPage(to);
        stripper.writeText(doc, writer);

        writer.close();
    }
}
