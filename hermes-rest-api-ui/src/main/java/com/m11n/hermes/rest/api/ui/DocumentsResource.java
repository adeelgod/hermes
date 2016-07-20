package com.m11n.hermes.rest.api.ui;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Named;
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
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.google.common.collect.ImmutableMap;
import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.model.DocumentsDocuments;
import com.m11n.hermes.core.model.DocumentsPrintjob;
import com.m11n.hermes.core.model.DocumentsPrintjobItem;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.PrintDocumentsRequest;
import com.m11n.hermes.core.model.PrintJob;
import com.m11n.hermes.core.model.PrintRequest;
import com.m11n.hermes.core.service.DocumentsService;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.DocumentsDocumentsRepository;
import com.m11n.hermes.persistence.DocumentsOrdersRepository;
import com.m11n.hermes.persistence.DocumentsPrintjobItemRepository;
import com.m11n.hermes.persistence.DocumentsPrintjobRepository;
import com.m11n.hermes.persistence.DocumentsTubGroupRepository;
import com.m11n.hermes.persistence.DocumentsTubRepository;
import com.m11n.hermes.persistence.FormRepository;

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
	private DocumentsPrintjobRepository documentsPrintjobRepository;
	
	@Inject
	private DocumentsTubGroupRepository documentsTubGroupRepository;
	
	@Inject
	private DocumentsTubRepository documentsTubRepository;
	
	@Inject
	private FormRepository formRepository;

	@Inject
	private AuswertungRepository auswertungRepository;
	
	private static int batchSize = 100;
	
    @Inject
    @Named("jdbcTemplateAuswertung")
    protected NamedParameterJdbcTemplate jdbcTemplate;
    
	@GET
	@Path("/generate/initial")
	public synchronized Response generateInitial() {
		documentsService.createPicklist(1);
		return Response.ok().build();
	}
	
	@POST
	@Path("/get_invoice")
	public Response getInvoice(final Map<String, Object> req) {
		try {
			List<String> orderIds = new LinkedList<>();
			orderIds.add((String) req.get("orderId"));
			documentsService.getInvoices(orderIds, null);
			logger.info("Added order {}", orderIds);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("/create")
	public synchronized Response create(final Map<String, Object> req) {
		logger.debug("Creating Printjob");
		try {
			DocumentsPrintjob printjob = new DocumentsPrintjob();
			printjob.setStatus("creating");
			documentsPrintjobRepository.save(printjob);
			// fill orders
			List<String> orderIds = (List<String>) req.get("orderIds");
			for (int i = 0; i < orderIds.size(); i+=batchSize) {
				logger.debug("Creating Orders {} to {}", i, i + batchSize);
				Form form = formRepository.findByName("create_documents_orders");
				Map<String, Object> params = ImmutableMap.<String, Object>builder()
						.put("printjobId", printjob.getId())
						.put("ids", orderIds.subList(i, Math.min(orderIds.size(), i + batchSize)))
						.build();
				jdbcTemplate.update(form.getSqlStatement(), params);
				logger.debug(form.getSqlStatement());
				logger.debug(params.toString());
			}
			Form form = formRepository.findByName("update_order_type");
			Map<String, Object> params = ImmutableMap.<String, Object>builder()
					.put("printjobId", printjob.getId())
					.build();
			jdbcTemplate.update(form.getSqlStatement(), params);
			
			documentsService.createPicklist(printjob.getId());
			printjob.setStatus("planned");
			documentsPrintjobRepository.save(printjob);
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return Response.ok().build();
	}
	
	@POST
	@Path("/print")
	public synchronized Response print(final PrintDocumentsRequest req) {
		try {
			Integer printjobId = req.getPrintjobId();
			logger.debug("Printing Printjob " + printjobId + ": " + req.getPrintjobItems().size() + " items");
			if (req.getPrintjobItems().size() > 0) {
				long maxGroup = documentsPrintjobItemRepository.countGroups(printjobId);
				Set<Integer> printjobItemIds = new HashSet<>(req.getPrintjobItems());
				for (int groupNo = 0; groupNo <= maxGroup; groupNo++) {
					logger.debug("Processing Group " + groupNo);
					Set<Integer> currentIds = documentsPrintjobItemRepository.findPrintjobItemIds(printjobId, groupNo);
					currentIds.retainAll(printjobItemIds);
					
					documentsPrintjobItemRepository.resetPrintjobItems(printjobId, currentIds);;
					List<DocumentsPrintjobItem> printjobItems = documentsPrintjobItemRepository.findPrintjobItems(printjobId, groupNo);
					
					List<Integer> documentIds = new LinkedList<>();
					for (DocumentsPrintjobItem printjobItem : printjobItems) {
						documentIds.add(printjobItem.getDocumentId());
					}
					logger.debug("Get Documents " + documentIds);
					List<DocumentsDocuments> documents = documentsDocumentsRepository.findByIds(documentIds);
					Map<Integer, DocumentsDocuments> documentsById = new HashMap<>();
					for (DocumentsDocuments document : documents) {
						documentsById.put(document.getId(), document);
					}
					for (DocumentsPrintjobItem printjobItem : printjobItems) {
						DocumentsDocuments document = documentsById.get(printjobItem.getDocumentId());
						
						boolean success = false;
						// TODO correct method for future<boolean>?
						success = print(document.getType().toUpperCase(), document.getPathPrint()).get();
						if (success) {
							printjobItem.setStatus("printed");
							if (printjobItem.getPrintedAt() == null) {
								printjobItem.setPrintedAt(new Date());
							}
							document.setNumPrinted(document.getNumPrinted() + 1);
							logger.debug("Saving document " + document);
							if (document.orderId() != null) {
								auswertungRepository.timestampPrint(document.getOrderId());
							}
							documentsDocumentsRepository.save(document);
						} else {
							printjobItem.setStatus("error");
						}
						logger.debug("Saving printjob " + printjobItem);
						documentsPrintjobItemRepository.save(printjobItem);
					}
				}
			}
			DocumentsPrintjob printjob = documentsPrintjobRepository.findOne(printjobId);
			printjob.setStatus("finished");
			printjob.setPrintedAt(new Date());
			documentsPrintjobRepository.save(printjob);
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
	
    private Future<Boolean> print(String type, String filename) {
        running.incrementAndGet();

        return executor.submit(new Callable<Boolean>() {
            boolean success = false;
            @Override
            public Boolean call() {
                try {
                    if (Thread.interrupted()) {
                        logger.warn("Skipping print request");
                        return false;
                    }
                    Properties p = PropertiesUtil.getProperties();

                    String dir = p.getProperty("hermes.print.dir");
                    boolean fast = StringUtils.isEmpty(p.getProperty("hermes.printer.fast")) ? false : Boolean.valueOf(p.getProperty("hermes.printer.fast"));

                    DocumentType documentType = DocumentType.valueOf(type);

                    String printer = null;

                    if (documentType.equals(DocumentType.INVOICE)) {
                        printer = p.getProperty("hermes.printer.invoice");
                    } else if (documentType.equals(DocumentType.LABEL)) {
                        printer = p.getProperty("hermes.printer.label");
                    } else if (documentType.equals(DocumentType.REPORT)) {
                        printer = p.getProperty("hermes.printer.report");
                    } else if (documentType.equals(DocumentType.PICKLIST)) {
                    	printer = p.getProperty("hermes.printer.picklist");
                    }

                    print(documentType, printer, filename, fast);
                    success = true;
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
