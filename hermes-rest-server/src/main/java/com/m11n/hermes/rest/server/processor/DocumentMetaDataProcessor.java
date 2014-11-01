package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.HermesConstants;
import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.service.PdfService;
import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.InputStream;

@Component
public class DocumentMetaDataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentMetaDataProcessor.class);

    @Inject
    private PdfService pdfService;

    public void process(Message message, @Header("CamelFileName") String fileName, @Body InputStream is) throws Exception {
        // TODO: implement this

        if(fileName.startsWith("invoice")) {
            String orderId = pdfService.value(is, 1, "Bestellnummer");
            message.setHeader(HermesConstants.HERMES_DOCUMENT_TYPE, DocumentType.INVOICE.name());
            message.setHeader(HermesConstants.HERMES_ORDER_ID, orderId);
        } else if(fileName.startsWith("label")) {
            String orderId = pdfService.value(is, 1, "Referenznr.");
            message.setHeader(HermesConstants.HERMES_DOCUMENT_TYPE, DocumentType.LABEL.name());
            message.setHeader(HermesConstants.HERMES_ORDER_ID, orderId);
        } else {
            logger.error("Document not recognized: {}", fileName);
        }
    }
}
