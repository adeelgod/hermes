package com.m11n.hermes.service.print;

import com.activetree.common.conversion.DocConverter;
import com.activetree.pdfprint.SilentPdfConverter;
import com.m11n.hermes.core.model.Printer;
import com.m11n.hermes.core.model.PrinterAttribute;
import com.m11n.hermes.core.model.PrinterAttributeCategory;
import com.m11n.hermes.core.model.PrinterStatus;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.DocumentLogRepository;
import com.qoppa.pdfPrint.PDFPrint;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.faceless.pdf2.PDF;
import org.faceless.pdf2.PDFParser;
import org.faceless.pdf2.PDFReader;
import org.ghost4j.Ghostscript;
import org.ghost4j.GhostscriptException;
import org.ghost4j.GhostscriptRevision;
import org.ghost4j.display.ImageWriterDisplayCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import java.awt.image.BufferedImage;
import java.awt.print.Pageable;
import java.awt.print.PrinterJob;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class DefaultPrinterService implements PrinterService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPrinterService.class);

    //private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class, Copies.class, PageRanges.class, JobSheets.class, Chromaticity.class);
    private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class);

    private String printQueueStatus;

    private List<Printer> printers = new CopyOnWriteArrayList<>();

    @Inject
    private DocumentLogRepository documentLogRepository;

    @PostConstruct
    public void init() {
        try {
            GhostscriptRevision revision = Ghostscript.getRevision();
            logger.info("############## GHOSTSCRIPT: {}", revision.getProduct());
            logger.info("############## GHOSTSCRIPT: {}", revision.getNumber());
            logger.info("############## GHOSTSCRIPT: {}", revision.getRevisionDate());
            logger.info("############## GHOSTSCRIPT: {}", revision.getCopyright());
        } catch(Throwable t) {
            logger.warn(t.toString(), t);
        }
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
                DocFlavor[] flavors = printer.getSupportedDocFlavors();
                for(DocFlavor flavor : flavors) {
                    logger.debug("+++++++++++ DOCFLAVOR {}: {}", name, flavor);
                }
                return printer;
            }
        }

        return null;
    }

    public void setPrintQueueStatus(String printQueueStatus) {
        this.printQueueStatus = printQueueStatus;
    }

    public String getPrintQueueStatus() {
        return printQueueStatus;
    }

    public JobStatus print(String file, String printer) throws Exception {
        Properties p = PropertiesUtil.getProperties();

        PrintMethod m = PrintMethod.valueOf(p.getProperty("hermes.printer.method"));

        JobStatus status = JobStatus.UNKNOWN;

        switch(m) {
            case JAVA:
                status = printJava(file, printer);
                break;
            case IMAGE:
                status = printImage(file, printer);
                break;
            case PAGEABLE:
                status = printPageable(file, printer);
                break;
            case PDFBOX:
                status = printPdfbox(file, printer);
                break;
            case GHOSTSCRIPT:
                status = printGhostscript(file, printer);
                break;
            case ACROBAT:
                status = printAcrobat(file, printer);
                break;
        }

        logger.debug("###################################### JOB DONE 1: {} ({}) - {}", file, status, m);

        return status;
    }

    private JobStatus printJava(String file, String printer) throws Exception {
        PrintService service = printer(printer);

        DocPrintJob job = service.createPrintJob();
        HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        attributes.add(new Copies(1));
        //attributes.add(OrientationRequested.PORTRAIT);
        //attributes.add(Chromaticity.MONOCHROME);
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        InputStream fis = new FileInputStream(file);

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        Doc doc = new SimpleDoc(fis, flavor, null);
        job.print(doc, attributes);
        JobStatus status = watcher.waitForDone();

        IOUtils.closeQuietly(fis);

        return status;
    }

    private JobStatus printPageable(String file, String printer) throws Exception {
        PrintService service = printer(printer);

        DocPrintJob job = service.createPrintJob();
        HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        Pageable pageable;

        // pdfbox
        // needs a job...
        //pageabel = new PDPageable(PDDocument.load(file), j);


        // swinglabs
        // NOTE: more infos here http://dendro.cornell.edu/svn/corina/trunk/src/edu/cornell/dendro/corina/util/pdf/PrintablePDF.java

        //ByteBuffer buf = ByteBuffer.wrap(org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(file)));
        //pageable = new SwinglabsPageable(new PDFFile(buf));


        // smartj
        // NOTE: more infos here https://www.activetree.com/online/services/pdf/pdf_viewer.jsp

        //int pageScaling = AtPdfStreamPrinter.PAGE_SCALING_FIT_TO_PRINTABLE_AREA;
        //PageFormat defaultPageFormat = MediaUtil.getSelectedPageFormat(MediaSizeName.ISO_A2);
        //AtPdfStreamPrinter p = new AtPdfStreamPrinter();
        //pageable = p.getPageable(file, defaultPageFormat, service, pageScaling);
        //testPng(file);


        // bof
        // NOTE: more infos here http://bfo.com/blog/2012/02/15/using_java_to_print_pdf_documents.html

        //pageable = new PDFParser(new PDF(new PDFReader(new File(file))));


        // quoppa
        //PDFPrint pdfPrint = new PDFPrint(fileName, null);
        //pdfPrint.print(printer, new PrintSettings());
        PrinterJob j = PrinterJob.getPrinterJob();
        j.setPrintService(service);
        pageable = new PDFPrint(file, null).getPageable(j);
        j.setPageable(pageable);
        //j.print(attributes);


        // icepdf
        // TODO: TBD


        Doc doc = new SimpleDoc(pageable, DocFlavor.SERVICE_FORMATTED.PAGEABLE, null);
        job.print(doc, attributes);
        JobStatus status = watcher.waitForDone();

        //PrinterJob pj = PrinterJob.getPrinterJob();
        //pj.setPrintService(service);
        //pj.setPageable(pageable);
        //pj.print(attributes);
        //JobStatus status = JobStatus.COMPLETED;

        return status;
    }

    /**
    private Pageable getPagesToPrint(Pageable pages) {
        DocPageable pagesToPrint = new DocPageable();
        for (int i=0; pages != null && i < pages.getNumberOfPages(); i++) {
            Printable aPage = pages.getPrintable(i);
            PageFormat aPageFormat = pages.getPageFormat(i);
            //Check if you want to print this page
            //Assumune: printing all pages
            pagesToPrint.append(aPage, aPageFormat);
        }
        return pagesToPrint;
    }
     */

    private void testPng(String file) throws Exception {
        DocConverter converter = new SilentPdfConverter();

        converter.setAttribute(DocConverter.DOC_TYPE, DocConverter.PNG);
        converter.setAttribute(DocConverter.DOC_LIST, "[" + file + "]");
        converter.setAttribute(DocConverter.OUTPUT_DIRECTORY, "/tmp/freak");
        converter.setAttribute(DocConverter.NAME_PREFIX, file.substring(file.lastIndexOf("/")+1));
        //converter.setAttribute(DocConverter.OUTPUT_STREAM, "/tmp/freak/" + file.substring(file.lastIndexOf("/")+1));
        //converter.setAttribute(DocConverter.RESIZE_FACTOR, 1.0);
        converter.setAttribute(DocConverter.PAPER_WIDTH, 826);
        converter.setAttribute(DocConverter.PAPER_HEIGHT, 1169);
        //converter.setAttribute(DocConverter.AUTO_MATCH_OUTPUT_TO_PAGE_SIZE, Boolean.TRUE);
        converter.setAttribute(DocConverter.DEBUG, Boolean.TRUE);

        converter.start();

        PrintService service = printer("PDF");

        DocPrintJob job = service.createPrintJob();
        HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
        attributes.add(new Copies(1));
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        Doc doc = new SimpleDoc(new FileInputStream("/tmp/freak/page_1_1.png"), DocFlavor.INPUT_STREAM.PNG, null);
        job.print(doc, attributes);
        watcher.waitForDone();
    }

    private JobStatus printPdfbox(String file, String printer) throws Exception {
        PrintService service = printer(printer);

        PrinterJob j = PrinterJob.getPrinterJob();
        j.setCopies(1);
        j.setJobName(UUID.randomUUID().toString() + ".pdf");
        j.setPrintService(service);

        PDDocument pdf = PDDocument.load(file);
        pdf.silentPrint(j);

        // version 2.0.0-SNAPSHOT
        //PDFPrinter p = new PDFPrinter(pdf, j);
        //p.silentPrint();

        logger.debug("###################################### JOB CANCELLED 1: {}", j.isCancelled());

        //pdf.close();

        return JobStatus.COMPLETED;  // TODO: improve this
    }

    private JobStatus printImage(String file, String printer) throws Exception {
        if(new File(file).exists()) {
            Properties p = PropertiesUtil.getProperties();
            String format = p.getProperty("hermes.printer.method.image.format").toLowerCase();

            DocFlavor.INPUT_STREAM type = DocFlavor.INPUT_STREAM.AUTOSENSE;

            if(format.equals("png")) {
                type = DocFlavor.INPUT_STREAM.PNG;
            } else if(format.equals("jpg")) {
                type = DocFlavor.INPUT_STREAM.JPEG;
            } else if(format.equals("gif")) {
                type = DocFlavor.INPUT_STREAM.GIF;
            }

            PrintService service = printer(printer);

            DocPrintJob job = service.createPrintJob();
            HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            PDDocument pdfDoc = PDDocument.load(file);
            List<PDPage> pages = pdfDoc.getDocumentCatalog().getAllPages();

            for(PDPage page : pages) {
                BufferedImage image = page.convertToImage();
                ImageIO.write(image, format, bos);

                PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
                attributes.add(new Copies(1));
                attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

                Doc doc = new SimpleDoc(new ByteArrayInputStream(bos.toByteArray()), type, null);
                job.print(doc, attributes);
                watcher.waitForDone();
            }
        }

        return JobStatus.COMPLETED;
    }

    private JobStatus printAcrobat(String file, String printer) throws Exception {
        // TODO: test this

        try{
            Properties p = PropertiesUtil.getProperties();
            Runtime.getRuntime().exec(p.getProperty("hermes.printer.method.acrobat") + " " + file);
            return JobStatus.COMPLETED;
        }catch(Exception e){
            e.printStackTrace();
        }

        return JobStatus.UNKNOWN;
    }

    private JobStatus printGhostscript(String file, String printer) throws Exception {
        // TODO: test this

        // NOTE: see here for details http://pnm2ppa.sourceforge.net/PPA_networking/PPA_networking-4.html

        Properties p = PropertiesUtil.getProperties();
        String[] args = (p.getProperty("hermes.printer.method.ghostscript") + " -sOutputFile=\"%printer%" + printer + "\"").trim().split(" ");

        for(String arg : args) {
            logger.info("##################### GHOSTSCRIPT ARG: {}", arg);
        }

        try {
            Ghostscript gs = Ghostscript.getInstance();

            //create display callback (capture display output pages as images)
            ImageWriterDisplayCallback displayCallback = new ImageWriterDisplayCallback();

            //set display callback
            //gs.setDisplayCallback(displayCallback);

            gs.initialize(args);
            gs.runFile(file);
            gs.exit();

            return JobStatus.COMPLETED;
        } catch (GhostscriptException e) {
            logger.error(e.toString(), e);
        }

        return JobStatus.UNKNOWN;
    }

    public void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception {
        DocPrintJob job = printer(printer).createPrintJob();
        HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

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
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        InputStream fis = new FileInputStream(file);

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        Doc doc = new SimpleDoc(fis, flavor, null);
        job.print(doc, attributes);

        JobStatus status = watcher.waitForDone();
        logger.debug("###################################### JOB DONE 2: {} ({})", file, status);

        IOUtils.closeQuietly(fis);
    }

    /**
    public void printxx(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception {
        DocPrintJob job = printer(printer).createPrintJob();
        job.addPrintJobListener(new HermesJobListener());

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

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
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        InputStream fis = new FileInputStream(file);

        DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PAGEABLE;

        //Doc doc = new SimpleDoc(new HermesPageable(PDDocument.load(file)), flavor, null);
        //job.print(doc, attributes);
        //fis.close();
    }

    public void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintService(printer(printer));
        job.setJobName(UUID.randomUUID().toString() + ".pdf");

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

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

        PDFPrinter pdfPrinter = new PDFPrinter(PDDocument.load(file));
        pdfPrinter.print(job, attributes);
    }
     */

    public List<Printer> printers() {
        if(printers.isEmpty()) {
            PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);

            for (PrintService printer : printServices) {
                Printer p = new Printer(printer.getName());

                p.setStatus(status(printer.getName()));

                for(Class category : printer.getSupportedAttributeCategories()) {
                    Object o = printer.getSupportedAttributeValues(category, null, null);

                    boolean match = attributeCategories.contains(category);

                    //logger.debug("++++++++++++++++++++++++++++++++++++++++++++ CATEGORY: {} ({})", category.getName(), match);

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

    private static class HermesPrintJobWatcher {
        // true iff it is safe to close the print job's input stream
        boolean done = false;
        JobStatus status = JobStatus.UNKNOWN;

        HermesPrintJobWatcher(DocPrintJob job) {
            // Add a listener to the print job
            job.addPrintJobListener(new PrintJobAdapter() {
                public void printJobCanceled(PrintJobEvent pje) {
                    done(JobStatus.CANCELLED);
                }
                public void printJobCompleted(PrintJobEvent pje) {
                    done(JobStatus.COMPLETED);
                }
                public void printJobFailed(PrintJobEvent pje) {
                    done(JobStatus.FAILED);
                }
                public void printJobNoMoreEvents(PrintJobEvent pje) {
                    done(JobStatus.NO_MORE_EVENTS);
                }
                void done(JobStatus s) {
                    synchronized (HermesPrintJobWatcher.this) {
                        done = true;
                        status = s;
                        HermesPrintJobWatcher.this.notify();
                    }
                }
            });
        }

        public synchronized JobStatus waitForDone() {
            try {
                while (!done) {
                    wait();
                }
            } catch (InterruptedException e) {
                // ignore
            }

            return status;
        }
    }

    /**
    private static class HermesPageable implements Pageable {
        private final List<PDPage> pages;
        DocPrintJob job;

        public HermesPageable(PDDocument document) throws IllegalArgumentException, PrinterException {
            this.pages = new ArrayList();
            if(document != null) {
                if(!document.getCurrentAccessPermission().canPrint()) {
                    throw new PrinterException("You do not have permission to print this document");
                } else {
                    document.getDocumentCatalog().getPages().getAllKids(this.pages);
                }
            } else {
                throw new IllegalArgumentException("PDPageable(" + document + ")");
            }
        }

        @Override
        public int getNumberOfPages() {
            return this.pages.size();
        }

        @Override
        public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
            PageFormat format = new PageFormat();
            PDPage page = this.pages.get(pageIndex);
            Dimension media = page.findMediaBox().createDimension();
            Dimension crop = page.findCropBox().createDimension();
            double diffWidth = 0.0D;
            double diffHeight = 0.0D;
            if(!media.equals(crop)) {
                diffWidth = (media.getWidth() - crop.getWidth()) / 2.0D;
                diffHeight = (media.getHeight() - crop.getHeight()) / 2.0D;
            }

            Paper paper = format.getPaper();
            if(media.getWidth() < media.getHeight()) {
                format.setOrientation(1);
                paper.setImageableArea(diffWidth, diffHeight, crop.getWidth(), crop.getHeight());
            } else {
                format.setOrientation(0);
                paper.setImageableArea(diffHeight, diffWidth, crop.getHeight(), crop.getWidth());
            }

            format.setPaper(paper);
            return format;
        }

        @Override
        public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
            return this.pages.get(pageIndex);
        }
    }
    */
}
