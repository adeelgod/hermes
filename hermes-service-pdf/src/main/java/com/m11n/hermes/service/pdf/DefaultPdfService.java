package com.m11n.hermes.service.pdf;

import com.m11n.hermes.core.model.Printer;
import com.m11n.hermes.core.model.PrinterAttribute;
import com.m11n.hermes.core.model.PrinterAttributeCategory;
import com.m11n.hermes.core.model.PrinterStatus;
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

import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DefaultPdfService implements PdfService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPdfService.class);

    //@Inject
    //private ProducerTemplate producer;

    //private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class, Copies.class, PageRanges.class, JobSheets.class, Chromaticity.class);
    private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class);

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
                return l.substring(fieldName.length(), l.length()).trim();
            }
        }

        return null;
    }

    public PDDocument split(PDDocument doc, int page) throws Exception {
        Splitter splitter = new Splitter();
        splitter.setStartPage(page);
        splitter.setEndPage(page);
        List<PDDocument> result = splitter.split(doc);

        return result.get(0);
    }

    public PrinterStatus status(String printerName) {
        PrintService printer = printer(printerName);

        if(printer!=null) {
            AttributeSet attributes = printer.getAttributes();
            Object state = attributes.get(PrinterState.class);
            Object reason = attributes.get(PrinterStateReason.class);

            return new PrinterStatus(state==null ? "" : state.toString(), reason==null ? "" : reason.toString());
        }

        return null;
    }

    private PrintService printer(String name) {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            if(printer.getName().equals(name)) {
                return printer;
            }
        }

        return null;
    }

    public void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception {
        //String dest = String.format("lpr://localhost/%s?copies=%s&flavor=DocFlavor.INPUT_STREAM&mimeType=AUTOSENSE&mediaTray=%s", printer, copies, mediaId);
        //logger.info("=================== PRINTER: {}", dest);
        //producer.sendBody(dest, new FileInputStream(file));

        DocPrintJob job = printer(printer).createPrintJob();
        job.addPrintJobListener(new HermesJobListener());

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        //attributes.add(MediaTray.BOTTOM);
        if(OrientationRequested.PORTRAIT.toString().equals(orientation)) {
            attributes.add(OrientationRequested.PORTRAIT);
        } else if(OrientationRequested.LANDSCAPE.toString().equals(orientation)) {
            attributes.add(OrientationRequested.LANDSCAPE);
        } else if(OrientationRequested.REVERSE_PORTRAIT.toString().equals(orientation)) {
            attributes.add(OrientationRequested.REVERSE_PORTRAIT);
        } else if(OrientationRequested.REVERSE_LANDSCAPE.toString().equals(orientation)) {
            attributes.add(OrientationRequested.REVERSE_LANDSCAPE);
        }
        attributes.add(new Copies(copies));
        attributes.add(new PageRanges(pageRange));
        attributes.add(Chromaticity.MONOCHROME);
        attributes.add(PrintQuality.DRAFT);
        attributes.add(new JobName(file, null));

        InputStream fis = new FileInputStream(file);

        //DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        //DocFlavor.SERVICE_FORMATTED.PAGEABLE;
        DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;

        Doc doc = new SimpleDoc(fis, flavor, null);
        job.print(doc, attributes);
        fis.close();
    }

    public List<Printer> printers() {
        List<Printer> printers = new ArrayList<>();

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService printer : printServices) {
            Printer p = new Printer(printer.getName());

            p.setStatus(status(printer.getName()));

            for(Class category : printer.getSupportedAttributeCategories()) {
                Object o = printer.getSupportedAttributeValues(category, null, null);

                boolean match = attributeCategories.contains(category);

                logger.debug("++++++++++++++++++++++++++++++++++++++++++++ CATEGORY: {} ({})", category.getName(), match);

                if (o != null && o.getClass().isArray() && match) {
                    PrinterAttributeCategory attributeCategory = new PrinterAttributeCategory(category.getName());
                    p.addAttributeCategory(attributeCategory);

                    for (Attribute attribute : (Attribute[]) o) {
                        String value = null;

                        /**
                        if(attribute instanceof EnumSyntax) {
                            //value = ((EnumSyntax)attribute).getValue() + "";
                            value = attribute.toString();
                        } else if(attribute instanceof SetOfIntegerSyntax) {
                            //value = Arrays.toString(((SetOfIntegerSyntax) attribute).getMembers());
                            value = attribute.toString();
                        }
                         */
                        if(attribute instanceof EnumSyntax) {
                            value = attribute.toString();
                        }
                        attributeCategory.addAttribute(new PrinterAttribute(attribute.getName(), value, attribute.getCategory().getName()));
                    }
                }
            }

            printers.add(p);
        }
        return printers;
    }

    private static class HermesJobListener implements PrintJobListener {
        @Override
        public void printDataTransferCompleted(PrintJobEvent pje) {
            logger.debug("####################### JOB - TRANSFERRED: {}", pje.getPrintJob().getPrintService().getName());
        }

        @Override
        public void printJobCompleted(PrintJobEvent pje) {
            logger.debug("####################### JOB - COMPLETED: {}", pje.getPrintJob().getPrintService().getName());
        }

        @Override
        public void printJobFailed(PrintJobEvent pje) {
            logger.debug("####################### JOB - FAILED: {}", pje.getPrintJob().getPrintService().getName());
        }

        @Override
        public void printJobCanceled(PrintJobEvent pje) {
            logger.debug("####################### JOB - CANCELLED: {}", pje.getPrintJob().getPrintService().getName());
        }

        @Override
        public void printJobNoMoreEvents(PrintJobEvent pje) {
            logger.debug("####################### JOB - MORE: {}", pje.getPrintJob().getPrintService().getName());
        }

        @Override
        public void printJobRequiresAttention(PrintJobEvent pje) {
            logger.debug("####################### JOB - ATTENTION: {}", pje.getPrintJob().getPrintService().getName());
        }
    }
}
