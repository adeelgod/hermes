package com.m11n.hermes.rest.server.processor;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.m11n.hermes.core.model.DocumentLog;
import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.service.DocumentsService;
import com.m11n.hermes.core.service.PdfService;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.persistence.DocumentLogRepository;

@Component
public class DocumentSplitProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DocumentSplitProcessor.class);

    @Inject
    private PdfService pdfService;

    @Inject
    private SshService sshService;
    
    @Inject
    private DocumentsService documentsService;

    @Inject
    private DocumentLogRepository documentLogRepository;

    @Value("${hermes.result.dir}")
    private String dir;

    @Value("${hermes.server.result.dir}")
    private String serverResultDir;

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

    private synchronized void processSingle(String filePath, String type, String orderId) {
        String remoteFilename = documentsService.getFilenameRemote(type, orderId);
        String remotePath = documentsService.getPathRemote(orderId);
        try {
        	try {
	            sshService.connect();
				int status = sshService.exec("mkdir -p " + remotePath + " && chmod 774 " + remotePath + " -R");
	            sshService.upload(filePath, remoteFilename);
	            boolean success = documentsService.create(type, orderId, remoteFilename, sshService, true);
	            if (!(status == 0)) {
	            	logger.error("Could not copy PDF to server: {}", remoteFilename);
	            }
	            if (!success) {
	            	logger.error("Could not add Document: {}", remoteFilename);
	            }
	        } catch (Exception e) {
	            logger.error(e.toString(), e);
	        } finally {
	            sshService.disconnect();
	        }
	    } catch(Throwable t) {
	        logger.error("XXXXX: {} ({})", t.getMessage(), filePath);
	        logger.error(t.toString(), t);
	    }

    }
    
    public synchronized void process(File f) {
        String fileName = f.getName();
        String filePath = f.getAbsolutePath();
        
        logger.debug("Name, Path: {}, {}", fileName, filePath);
        Pattern p = Pattern.compile("^([\\d-]+)_(.+)\\.pdf$");
        Matcher m = p.matcher(fileName);
        if (m.find()) {
        	String orderId = m.group(1);
        	String type = m.group(2);
        	logger.debug("Process single file, orderId: {}, type: {}", orderId, type);
        	processSingle(filePath, type, orderId);
        	return;
        } else {
        	this.processMultiple(fileName, filePath);
        }
    }
    
    private void processMultiple(String fileName, String filePath) {
        try {
            PDDocument parent = PDDocument.load(filePath);

            //logger.debug("!!!!!!!!!!!!!!!!!!!!!!!! PROCESSING: {} - {} ({})", fileName, parent.getNumberOfPages(), filePath);

            for(int i=1; i<=parent.getNumberOfPages(); i++) {
                // NOTE: splitting one by one is the best approach without concurrency problems
                List<PDDocument> documents = pdfService.split(parent, i);

                PDDocument document = documents.get(0);

                DocumentLog documentLog = null;

                if(fileName.toLowerCase().contains("invoice")) {
                    documentLog = getDocumentLog(pdfService.value(document, 1, invoiceOrderIdField), DocumentType.INVOICE.name());
                    documentLog.setDocumentId(pdfService.value(document, 1, invoiceIdField));
                    documentLog.setType(DocumentType.INVOICE.name());
                } else if (fileName.toLowerCase().contains("label")) {
                    documentLog = getDocumentLog(pdfService.value(document, 1, labelOrderIdField), DocumentType.LABEL.name());
                    documentLog.setDocumentId(pdfService.value(document, 1, labelIdField));
                    documentLog.setType(DocumentType.LABEL.name());
                } else {
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!! FAILED: {} ({})", fileName, filePath);
                }

                //logger.debug("!!!!!!!!!!!!!!!!!!!!!!!! PROCESSING: {} - {}", fileName, documentLog);

                if(documentLog !=null && documentLog.getOrderId()!=null) {
                    String resultDir = dir + "/" + PathUtil.segment(documentLog.getOrderId());
                    File t = new File(resultDir);
                    if(!t.exists()) {
                        t.mkdirs();
                    }
                    String fileNameResult = resultDir + "/" + documentLog.getType().toLowerCase() + ".pdf";
                    document.save(fileNameResult);

                    String orderId = documentLog.getOrderId();
                    String type = documentLog.getType().toLowerCase();
                    String remoteFilename = documentsService.getFilenameRemote(type, orderId);
                    String remotePath = documentsService.getPathRemote(orderId);
                    // copy to server
                    try {
                        sshService.connect();
                        int status = sshService.exec("mkdir -p " + remotePath + " && chmod 774 " + remotePath + " -R");
                        sshService.upload(fileNameResult, remoteFilename);
                        boolean success = documentsService.create(type, orderId, remoteFilename, sshService, true);
                        if (!(status == 0)) {
                        	logger.error("Could not copy PDF to server: {}", remoteFilename);
                        }
                        if (!success) {
                        	logger.error("Could not add Document: {}", remoteFilename);
                        }
                    } catch (Exception e) {
                        logger.error(e.toString(), e);
                    } finally {
                        sshService.disconnect();
                    }

                    documentLog.setProcessedAt(new Date());
                    documentLogRepository.save(documentLog);
                    logger.debug("##################### LOG ENTRIES: {}", documentLog);
                } else {
                    logger.error("!!!!!!!!!!!!!!!!!!!!!!!! FAILED: {} - {}", fileName, documentLog);
                    File t = new File(dir + "/error/");
                    if(!t.exists()) {
                        t.mkdirs();
                    }
                    //String type = documentLog==null ? "unknown" : documentLog.getType().toLowerCase();
                    document.save(dir + "/error/" + fileName + "-" + i + ".pdf");
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
