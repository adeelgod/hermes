package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.DocumentLog;
import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.service.PdfService;
import com.m11n.hermes.persistence.DocumentLogRepository;
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
    private DocumentLogRepository documentLogRepository;

    @Value("${hermes.result.dir}")
    private String dir;

    @Value("${hermes.invoice.order.field}")
    private String invoiceOrderIdField;

    @Value("${hermes.label.order.field}")
    private String labelOrderIdField;

    @Value("${hermes.invoice.id.field}")
    private String invoiceIdField;

    @Value("${hermes.label.id.field}")
    private String labelIdField;

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

    public void process(File f) {
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

                DocumentLog documentLog = null;

                if(prefix.equals("invoice")) {
                    documentLog = getDocumentLog(pdfService.value(document, 1, invoiceOrderIdField), DocumentType.INVOICE.name());
                    documentLog.setDocumentId(pdfService.value(document, 1, invoiceIdField));
                    documentLog.setType(DocumentType.INVOICE.name());
                } else if(prefix.equals("label")) {
                    documentLog = getDocumentLog(pdfService.value(document, 1, labelOrderIdField), DocumentType.LABEL.name());
                    documentLog.setDocumentId(pdfService.value(document, 1, labelIdField));
                    documentLog.setType(DocumentType.LABEL.name());
                }

                if(documentLog !=null && documentLog.getOrderId()!=null) {
                    String tmpDir = dir + "/" + documentLog.getOrderId();
                    File t = new File(tmpDir);
                    if(!t.exists()) {
                        t.mkdirs();
                    }
                    String fileNameTmp = tmpDir + "/" + prefix + ".pdf";
                    //logger.debug("##################### SAVING: {} ({})", fileNameTmp, filePath);
                    document.save(fileNameTmp);

                    documentLog.setProcessedAt(new Date());
                    documentLogRepository.save(documentLog);
                    logger.debug("##################### LOG ENTRIES: {}", documentLogRepository.count());
                }

                // TODO: decide if we should store the not detected files too
            }
        } catch(Throwable t) {
            logger.error("XXXXX: {} ({})", t.getMessage(), filePath);
            logger.error(t.toString(), t);
        }
    }

    private DocumentLog getDocumentLog(String orderId, String type) {
        DocumentLog documentLog = documentLogRepository.findByOrderIdAndType(orderId, type);

        if(documentLog ==null) {
            documentLog = new DocumentLog();
            documentLog.setOrderId(orderId);
        }

        return documentLog;
    }
}
