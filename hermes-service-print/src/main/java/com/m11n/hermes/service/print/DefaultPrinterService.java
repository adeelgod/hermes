package com.m11n.hermes.service.print;

import com.m11n.hermes.core.model.*;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.persistence.PrinterLogRepository;
import org.apache.pdfbox.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import java.awt.print.PrinterJob;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class DefaultPrinterService implements PrinterService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPrinterService.class);

    //private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class, Copies.class, PageRanges.class, JobSheets.class, Chromaticity.class);
    private List<? extends Class> attributeCategories = Arrays.asList(OrientationRequested.class, Media.class, MediaTray.class);

    private String printQueueStatus;

    @Inject
    private PrinterLogRepository printerLogRepository;

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

    public void printSelected() throws Exception {
        // TODO: implement this
        logger.info("####################### PRINTING SELECTED...");

        int chargeSize = 20; // TODO: retrieve from configuration
        String resultDir = "./result"; // TODO: retrieve from configuration
        String printerInvoice = "PDF"; // TODO: retrieve from configuration
        String printerLabel = "PDF"; // TODO: retrieve from configuration

        int i = 0;

        for(PrinterLog printerLog : printerLogRepository.findBySelected(true)) {
            i++;

            JobStatus status = null;

            if((printerLog.getInvoicePrinted()==null || !printerLog.getInvoicePrinted()) && printerLog.getInvoice()) {
                status = print(resultDir + "/" + printerLog.getOrderId() + "/invoice.pdf", printerInvoice);
                // TODO: check status
                printerLog.setInvoicePrinted(true);
                printerLog.setInvoicePrintedAt(new Date());
                printerLog = printerLogRepository.save(printerLog);
            }
            if((printerLog.getLabelPrinted()==null || !printerLog.getLabelPrinted()) && printerLog.getLabel()) {
                status = print(resultDir + "/" + printerLog.getOrderId() + "/label.pdf", printerLabel);
                // TODO: check status
                printerLog.setLabelPrinted(true);
                printerLog.setLabelPrintedAt(new Date());
                printerLog = printerLogRepository.save(printerLog);
            }
            // TODO: check status
            printerLog.setSelected(false);
            printerLog = printerLogRepository.save(printerLog);

            if(i%chargeSize==0) {
                // TODO: what's the best way to print the report?!?
                //print(resultDir + "/" + printerLog.getOrderId() + "/label.pdf", printerLabel);
            }
        }
    }

    public void setPrintQueueStatus(String printQueueStatus) {
        this.printQueueStatus = printQueueStatus;
    }

    public String getPrintQueueStatus() {
        return printQueueStatus;
    }

    public JobStatus print(String file, String printer) throws Exception {
        PrinterJob.getPrinterJob().defaultPage();

        DocPrintJob job = printer(printer).createPrintJob();
        //job.addPrintJobListener(new HermesJobListener());
        HermesPrintJobWatcher watcher = new HermesPrintJobWatcher(job);

        PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        attributes.add(OrientationRequested.PORTRAIT);
        attributes.add(new Copies(1));
        attributes.add(Chromaticity.MONOCHROME);
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        InputStream fis = new FileInputStream(file);

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;

        Doc doc = new SimpleDoc(fis, flavor, null);
        job.print(doc, attributes);

        JobStatus status = watcher.waitForDone();
        logger.debug("###################################### JOB DONE: {} ({})", file, status);

        IOUtils.closeQuietly(fis);

        return status;
    }

    public void print(String file, String pageRange, String printer, String orientation, String mediaId, int copies) throws Exception {
        PrinterJob.getPrinterJob().defaultPage();

        DocPrintJob job = printer(printer).createPrintJob();
        //job.addPrintJobListener(new HermesJobListener());
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
        //attributes.add(Chromaticity.MONOCHROME);
        //attributes.add(PrintQuality.DRAFT);
        attributes.add(new JobName(UUID.randomUUID().toString() + ".pdf", null));

        InputStream fis = new FileInputStream(file);

        DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
        //DocFlavor flavor = DocFlavor.INPUT_STREAM.PDF;

        Doc doc = new SimpleDoc(fis, flavor, null);
        job.print(doc, attributes);

        JobStatus status = watcher.waitForDone();
        logger.debug("###################################### JOB DONE: {} ({})", file, status);

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
        List<Printer> printers = new ArrayList<>();

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
