package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.util.List;

@Component
public class DocumentSplitProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSplitProcessor.class);

    @Inject
    private PdfService pdfService;

    private String dir = "./result"; // TODO: make this configurable

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

                String orderId = null;

                if(prefix.equals("invoice")) {
                    orderId = pdfService.value(document, 1, "Bestellnummer");
                } else if(prefix.equals("label")) {
                    orderId = pdfService.value(document, 1, "Referenznr.");
                }

                if(orderId!=null) {
                    String tmpDir = dir + "/" + orderId;
                    File t = new File(tmpDir);
                    if(!t.exists()) {
                        t.mkdirs();
                    }
                    String fileNameTmp = tmpDir + "/" + prefix + ".pdf";
                    logger.debug("##################### SAVING: {} ({})", fileNameTmp, filePath);
                    document.save(fileNameTmp);
                }
            }

            // TODO: save transaction in database
        } catch(Throwable t) {
            logger.error("xxxxxxxxxx: {} ({})", t.getMessage(), filePath);
            logger.error(t.toString(), t);
        }
    }
}
