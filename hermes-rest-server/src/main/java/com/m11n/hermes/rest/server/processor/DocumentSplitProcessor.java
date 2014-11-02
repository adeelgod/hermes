package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.PrinterLog;
import com.m11n.hermes.core.service.PdfService;
import com.m11n.hermes.persistence.PrinterLogRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.util.Date;
import java.util.List;

@Component
public class DocumentSplitProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSplitProcessor.class);

    @Inject
    private PdfService pdfService;

    @Inject
    private PrinterLogRepository printerLogRepository;

    @Value("${hermes.result.dir}")
    private String dir;

    @Value("${hermes.invoice.field}")
    private String invoiceField;

    @Value("${hermes.label.field}")
    private String labelField;

    @PostConstruct
    public void init() {
        try {
            File f = new File(dir);

            if(!f.exists()) {
                f.mkdirs();
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    // TODO: find out if this is really necessary
    public synchronized void process(File f) {
        String fileName = f.getName();
        String filePath = f.getAbsolutePath();

        try {
            PDDocument parent = PDDocument.load(filePath);

            for(int i=1; i<=parent.getNumberOfPages(); i++) {
                // NOTE: splitting one by one is the best approach without concurrency problems
                List<PDDocument> documents = pdfService.split(parent, i);

                PDDocument document = documents.get(0);

                String prefix = "unknown";

                if(fileName.toLowerCase().contains("invoice")) {
                    prefix = "invoice";
                } else if(fileName.toLowerCase().contains("label")) {
                    prefix = "label";
                }

                PrinterLog printerLog = null;

                if(prefix.equals("invoice")) {
                    printerLog = getPrinterLog(pdfService.value(document, 1, invoiceField));
                    printerLog.setInvoice(Boolean.TRUE);
                } else if(prefix.equals("label")) {
                    printerLog = getPrinterLog(pdfService.value(document, 1, labelField));
                    printerLog.setLabel(Boolean.TRUE);
                }

                if(printerLog!=null && printerLog.getOrderId()!=null) {
                    String tmpDir = dir + "/" + printerLog.getOrderId();
                    File t = new File(tmpDir);
                    if(!t.exists()) {
                        t.mkdirs();
                    }
                    String fileNameTmp = tmpDir + "/" + prefix + ".pdf";
                    //logger.debug("##################### SAVING: {} ({})", fileNameTmp, filePath);
                    document.save(fileNameTmp);

                    printerLog.setProcessedAt(new Date());
                    printerLogRepository.save(printerLog);
                    logger.debug("##################### LOG ENTRIES: {}", printerLogRepository.count());
                }

                // TODO: decide if we should store the not detected files too
            }

            // TODO: save transaction in database
        } catch(Throwable t) {
            logger.error("XXXXX: {} ({})", t.getMessage(), filePath);
            logger.error(t.toString(), t);
        }
    }

    private PrinterLog getPrinterLog(String orderId) {
        PrinterLog printerLog = printerLogRepository.findByOrderId(orderId);

        if(printerLog==null) {
            printerLog = new PrinterLog();
            printerLog.setOrderId(orderId);
        }

        return printerLog;
    }
}
