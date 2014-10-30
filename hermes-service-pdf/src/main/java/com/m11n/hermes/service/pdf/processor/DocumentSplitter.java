package com.m11n.hermes.service.pdf.processor;

import com.m11n.hermes.core.service.PdfService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;

@Component
public class DocumentSplitter {

    @Inject
    private PdfService pdfService;

    private String baseDir = "/tmp/hermes"; // TODO: make this configurable

    @PostConstruct
    public void init() {
        File f = new File(baseDir);

        if(!f.exists()) {
            f.mkdirs();
        }
    }

    public void split(File file) throws Exception {
        PDDocument doc = pdfService.parse(new FileInputStream(file));

        int numberOfPages = doc.getNumberOfPages();

        for(int i=1; i<=numberOfPages; i++) {
            String orderId = pdfService.value(doc, i, "Bestellnummer:"); // TODO: make this configurable

            PDDocument page = pdfService.split(doc, i);

            String name = "unknown";

            // TODO: make this configurable; regex would be best
            if(file.getName().startsWith("invoice")) {
                name = "invoice";
            } else if(file.getName().startsWith("label")) {
                name = "label";
            }

            File targetDir = new File(baseDir + "/" + orderId);

            if(!targetDir.exists()) {
                targetDir.mkdirs();
            }

            page.save(baseDir + "/" + orderId + "/" +  name + ".pdf");

            // TODO: save transaction in database
        }
    }
}
