package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.service.PdfService;
import org.apache.camel.Header;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Component
public class DocumentSplitProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSplitProcessor.class);

    @Inject
    private PdfService pdfService;

    private String dir = "./extract"; // TODO: make this configurable

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

    public void process(@Header("CamelFileName") String fileName, @Header("CamelFilePath") String filePath, InputStream is) {
        try {
            logger.debug("##################### PATH: {}", filePath);

            List<PDDocument> documents = pdfService.split(PDDocument.load(filePath), 1);

            String prefix = "unknown-";

            if(fileName.toLowerCase().contains("invoice")) {
                prefix = "invoice-";
            } else if(fileName.toLowerCase().contains("label")) {
                prefix = "label-";
            }

            for(PDDocument document : documents) {
                String fileNameTmp = dir + "/" + prefix + UUID.randomUUID().toString() + ".pdf";
                logger.debug("##################### SAVING: {}", fileNameTmp);
                //document.close();
                document.save(fileNameTmp);
            }

            // TODO: save transaction in database
        } catch(Throwable t) {
            logger.error(t.getMessage());
            logger.error(t.toString(), t);
        }
    }
}
