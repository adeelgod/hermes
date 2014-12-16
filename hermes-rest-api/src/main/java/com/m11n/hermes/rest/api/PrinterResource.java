package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.model.PrintRequest;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/printers")
@Produces(APPLICATION_JSON)
public class PrinterResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterResource.class);

    @Inject
    private PrinterService printerService;

    @Inject
    private ReportService reportService;

    private String lastError;

    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private AtomicInteger running = new AtomicInteger(0);

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(printerService.printers()).cacheControl(cc).build();
    }

    @GET
    @Path("/print/status")
    @Produces(APPLICATION_JSON)
    public Response status() {
        return Response.ok(running.get()>0).build();
    }

    @GET
    @Path("/print/cancel")
    @Produces(APPLICATION_JSON)
    public synchronized Response cancel() throws Exception {
        executor.shutdownNow();
        executor = Executors.newSingleThreadExecutor();
        running.set(0);

        logger.info("!!!!!!!!!!!!!!!!!!!!! Printing cancelled.");
        return Response.ok().build();
    }

    @POST
    @Path("/print/all")
    @Produces(APPLICATION_JSON)
    public synchronized Response printAll(final PrintRequest req) throws Exception {
        // same order as javascript: reports per charge, invoice, label

        try {
            if(running.get()<=0) {
                Properties p = PropertiesUtil.getProperties();
                Integer chargeSize = req.getChargeSize()==null? Integer.valueOf(p.getProperty("hermes.charge.size")) : req.getChargeSize();

                int i=0;

                running.set(req.getOrderIds().size()-1);

                for(String orderId : req.getOrderIds()) {
                    if(i%chargeSize==0) {
                        print(new PrintRequest(DocumentType.REPORT.name(), orderId, req.getChargeSize()));
                    }
                    print(new PrintRequest(DocumentType.INVOICE.name(), orderId, req.getChargeSize()));
                    print(new PrintRequest(DocumentType.LABEL.name(), orderId, req.getChargeSize()));
                }
            } else {
                logger.warn("Please cancel first all running print jobs.");
            }
        } catch (Exception e) {
            if(e instanceof InterruptedException) {
                logger.warn("Print job cancelled.");
            } else {
                // TODO: store this in local variable
                logger.error(e.toString(), e);
            }
        }

        return Response.ok().build();
    }

    private void print(final PrintRequest req) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    if(Thread.interrupted()) {
                        logger.warn("Skipping print request: {}", req);
                        return;
                    }
                    Properties p = PropertiesUtil.getProperties();

                    String dir = p.getProperty("hermes.result.dir");
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

                    for (String orderId : req.getOrderIds()) {
                        if(Thread.interrupted()) {
                            logger.warn("Skipping print request: {}", req);
                            return;
                        }
                        if (documentType.equals(DocumentType.INVOICE) || documentType.equals(DocumentType.LABEL)) {
                            print(documentType, printer, dir + "/" + orderId + "/" + req.getType().toLowerCase() + ".pdf", fast);
                        } else if (documentType.equals(DocumentType.REPORT)) {
                            logger.info("******************************************** PRINT: REPORT ORDER_IDS {}", req.getOrderIds());

                            String[] templates = req.getTemplates().split("\\|");

                            for (String template : templates) {
                                if(Thread.interrupted()) {
                                    logger.warn("Skipping print request: {}", req);
                                    return;
                                }
                                String reportOutput = dir + "/reports/" + UUID.randomUUID().toString() + ".pdf";

                                Map<String, Object> params = new HashMap<>();
                                params.put("_order_ids", req.getOrderIds());

                                reportService.generate(template, params, "pdf", reportOutput);

                                print(documentType, printer, reportOutput, fast);

                                // cleanup
                                FileUtils.deleteQuietly(new File(reportOutput));
                            }
                        }
                    }

                } catch (Exception e) {
                    logger.error(e.toString(), e);
                } finally {
                    running.decrementAndGet();
                }
            }
        });
    }

    private void print(DocumentType type, String printer, String path, boolean fast) throws Exception {

        if(new File(path).exists()) {
            logger.info("******************************************** PRINT: printer:{} type:{} file:{} fast: {}", printer, type, path, fast);

            printerService.print(path, printer);

            if(!fast) {
                try {
                    Thread.sleep(1000); // TODO: make this configurable
                } catch (InterruptedException e) {
                    logger.warn("Stopped queue.");
                }

                logger.info("******************************************** PRINT: wakeup");
            }
        } else {
            logger.warn("******************************************** PRINT: file not found {}", path);
        }
    }
}
