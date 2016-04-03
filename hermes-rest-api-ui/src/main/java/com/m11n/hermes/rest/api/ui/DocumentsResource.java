package com.m11n.hermes.rest.api.ui;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.model.DocumentsDocuments;
import com.m11n.hermes.core.model.DocumentsPrintjobItem;
import com.m11n.hermes.core.model.PrintDocumentsRequest;
import com.m11n.hermes.core.model.PrintJob;
import com.m11n.hermes.core.model.PrintRequest;
import com.m11n.hermes.core.service.DocumentsService;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.DocumentsDocumentsRepository;
import com.m11n.hermes.persistence.DocumentsOrdersRepository;
import com.m11n.hermes.persistence.DocumentsPrintjobItemRepository;
import com.m11n.hermes.persistence.DocumentsTubGroupRepository;
import com.m11n.hermes.persistence.DocumentsTubRepository;

@Singleton
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentsResource {
	private static final Logger logger = LoggerFactory.getLogger(DocumentsResource.class);
	
    private AtomicInteger running = new AtomicInteger(0);
    
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    
    @Inject
    private PrinterService printerService;

    @Inject
    private ReportService reportService;

	@Inject
	private DocumentsOrdersRepository documentsOrdersRepository;
	
	@Inject
	private DocumentsDocumentsRepository documentsDocumentsRepository;
	
	@Inject
	private DocumentsService documentsService;
	
	@Inject
	private DocumentsPrintjobItemRepository documentsPrintjobItemRepository;
	
	@Inject
	private DocumentsTubGroupRepository documentsTubGroupRepository;
	
	@Inject
	private DocumentsTubRepository documentsTubRepository;
	
	@GET
	@Path("/generate")
	public synchronized Response generate() {
		documentsService.createPicklist(1);
		return Response.ok().build();
	}
	
	@POST
	@Path("/print")
	public synchronized Response print(final PrintDocumentsRequest req) {
		try {
			logger.debug("Printing Printjob " +req.getPrintjobId() + ": " + req.getPrintjobItems().size() + " items");
			if (req.getPrintjobItems().size() > 0) {
				documentsPrintjobItemRepository.resetPrintjobItems(req.getPrintjobId(), req.getPrintjobItems());
				List<DocumentsPrintjobItem> printjobItems = documentsPrintjobItemRepository.findPrintjobItems(req.getPrintjobId(), req.getPrintjobItems());
				List<Integer> documentIds = new LinkedList<>();
				for (DocumentsPrintjobItem printjobItem : printjobItems) {
					documentIds.add(printjobItem.getDocumentId());
				}
				List<DocumentsDocuments> documents = documentsDocumentsRepository.findByIds(documentIds);
				for (DocumentsPrintjobItem printjobItem : printjobItems) {
					printjobItem.setStatus("printed");
					printjobItem.setPrintedAt(new Date());
					documentsPrintjobItemRepository.save(printjobItem);
					//Future<Boolean> invoiceSuccess = print(new PrintJob(DocumentType.INVOICE.name(), orderId, req.getChargeSize()));
					//Future<Boolean> labelSuccess = print(new PrintJob(DocumentType.LABEL.name(), orderId, req.getChargeSize()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.ok().build();
	}
	
	@GET
	@Path("/status/create")
	public synchronized Response getStateCreate() {
		// TODO
		int printjobId = 1;
		long numNew = documentsOrdersRepository.countPlanned();
		long numGroupsCreated = documentsTubGroupRepository.countGroups(printjobId);
		long numGroupsProcessed = documentsPrintjobItemRepository.countGroups(printjobId);
		String message = "Collecting " + numNew + " orders.";
		if (numGroupsCreated > 0) {
			message = "Processing " + numGroupsProcessed + " of " + numGroupsCreated + " Groups.";
		}
		return Response.ok(message).build();
	}
	
	@GET
	@Path("/status/print")
	public synchronized Response getStatePrint() {
		// TODO
		int printjobId = 1;
		String message = "";
		return Response.ok(message).build();
	}
	
    private Future<Boolean> print(final PrintJob req) {
        running.incrementAndGet();

        return executor.submit(new Callable<Boolean>() {
            boolean success = false;
            @Override
            public Boolean call() {
                try {
                    if (Thread.interrupted()) {
                        logger.warn("Skipping print request: {}", req);
                        return false;
                    }
                    Properties p = PropertiesUtil.getProperties();

                    String dir = p.getProperty("hermes.print.dir");
                    boolean fast = StringUtils.isEmpty(p.getProperty("hermes.printer.fast")) ? false : Boolean.valueOf(p.getProperty("hermes.printer.fast"));

                    if (StringUtils.isEmpty(req.getTemplates())) {
                        req.setTemplates(p.getProperty("hermes.reporting.template.report"));
                    }

                    DocumentType documentType = DocumentType.valueOf(req.getType());

                    String printer = null;

                    if (documentType.equals(DocumentType.INVOICE)) {
                        printer = p.getProperty("hermes.printer.invoice");
                    } else if (documentType.equals(DocumentType.LABEL)) {
                        printer = p.getProperty("hermes.printer.label");
                    } else if (documentType.equals(DocumentType.REPORT)) {
                        printer = p.getProperty("hermes.printer.report");
                    }

                    if (documentType.equals(DocumentType.INVOICE) || documentType.equals(DocumentType.LABEL)) {
                        print(documentType, printer, dir + "/" + PathUtil.segment(req.getOrderId()) + "/" + req.getType().toLowerCase() + ".pdf", fast);
                        success = true;
                    } else if (documentType.equals(DocumentType.REPORT)) {
                        String[] templates = StringUtils.trimToEmpty(req.getTemplates()).split("\\|");

                        for (String template : templates) {
                            if (Thread.interrupted()) {
                                logger.warn("Skipping print request: {}", req);
                                return false;
                            }

                            logger.info("PRINT: REPORT PARAMS {} - {}", req.getParams(), template);

                            String reportOutput = dir + "/reports/" + UUID.randomUUID().toString() + ".pdf";

                            reportService.generate(template, req.getParams(), "pdf", reportOutput);

                            print(documentType, printer, reportOutput, fast);

                            success = true;

                            // cleanup
                            FileUtils.deleteQuietly(new File(reportOutput));
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                } finally {
                    running.decrementAndGet();
                }

                return success;
            }
        });
    }

    private void print(DocumentType type, String printer, String path, boolean fast) throws Exception {

        if(new File(path).exists()) {
            logger.info("PRINT: printer:{} type:{} file:{} fast: {} count: {}", printer, type, path, fast, running.get());

            printerService.print(path, printer);

            if(!fast) {
                try {
                    Thread.sleep(1000); // TODO: make this configurable
                } catch (InterruptedException e) {
                    logger.warn("Stopped queue.");
                }

                logger.info("PRINT: wakeup");
            }
        } else {
            throw new RuntimeException("PRINT: file not found " + path);
        }
    }

	
}
