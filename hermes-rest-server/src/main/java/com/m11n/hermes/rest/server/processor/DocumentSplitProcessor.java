package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
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

    //public void process(@Header("CamelFileName") String fileName, @Header("CamelFilePath") String filePath, InputStream is) {
    public synchronized void process(File f) {
        String fileName = f.getName();
        String filePath = f.getAbsolutePath();

        try {
            logger.debug("##################### PATH: {}", f.getAbsolutePath());

            //List<PDDocument> documents = pdfService.split(PDDocument.load(filePath), 1);
            List<PDDocument> documents = pdfService.split(new FileInputStream(f), 1);

            String prefix = "unknown";

            if(fileName.toLowerCase().contains("invoice")) {
                prefix = "invoice";
            } else if(fileName.toLowerCase().contains("label")) {
                prefix = "label";
            }

            for(PDDocument document : documents) {
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

    private void lock(String file) throws Exception {
        new File(file).createNewFile();
    }

    private void unlock(String file) throws Exception {
        new File(file).delete();
    }
}
