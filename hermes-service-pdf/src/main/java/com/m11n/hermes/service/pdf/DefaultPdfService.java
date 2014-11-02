package com.m11n.hermes.service.pdf;

import com.m11n.hermes.core.service.PdfService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.activation.FileDataSource;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

@Service
public class DefaultPdfService implements PdfService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPdfService.class);

    public PDDocument parse(InputStream is) throws Exception {
        PDFParser parser = new PDFParser(is);
        parser.parse();

        return parser.getPDDocument();
    }

    public String value(InputStream is, int page, String fieldName) throws Exception {
        PDDocument doc = parse(is);

        return value(doc, page, fieldName);
    }

    public String value(PDDocument doc, int page, String fieldName) throws Exception {
        Writer writer = new StringWriter();

        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(page);
        stripper.setEndPage(page);
        stripper.writeText(doc, writer);

        writer.close();

        LineIterator it = IOUtils.lineIterator(IOUtils.toInputStream(writer.toString()), Charset.forName("UTF-8"));

        while(it.hasNext()) {
            String l = it.next().trim();
            if(l.startsWith(fieldName)) {
                return l.substring(fieldName.length(), l.length()).replace(":", "").trim();
            }
        }

        return null;
    }

    public List<PDDocument> split(String file, int page) throws Exception {
        FileDataSource fd = new FileDataSource(file);

        return split(fd.getInputStream(), page);
    }

    public List<PDDocument> split(InputStream is, int page) throws Exception {
        PDFParser parser = new PDFParser(is);
        parser.parse();
        PDDocument doc = parser.getPDDocument();

        return split(doc, page);
    }

    public List<PDDocument> split(PDDocument doc, int page) throws Exception {
        Splitter splitter = new Splitter();
        //splitter.setSplitAtPage(page);
        splitter.setStartPage(page);
        splitter.setEndPage(page);

        return splitter.split(doc);
    }
}
