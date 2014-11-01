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
    public void testSplitInvoice() throws Exception {
        split("src/test/resources/invoice.pdf");
    }

    @Test
    public void testSplitLabel() throws Exception {
        split("src/test/resources/labels.pdf");
    }

    private void split(String file) throws Exception {
        FileDataSource fd = new FileDataSource(file);

        PDFParser parser = new PDFParser(fd.getInputStream());
        parser.parse();

        PDDocument doc = parser.getPDDocument();

        Splitter splitter = new Splitter();
        //splitter.setStartPage(1);
        //splitter.setEndPage(2);
        splitter.setSplitAtPage(2);
        List<PDDocument> result = splitter.split(doc);

        int i = 0;

        for(PDDocument d : result) {
            i++;
            d.save("target/" + new File(file).getName() + "-" + i + ".pdf");
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
