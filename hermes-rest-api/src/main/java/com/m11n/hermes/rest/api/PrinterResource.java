package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.model.PrintJob;
import com.m11n.hermes.core.model.PrintRequest;
import com.m11n.hermes.core.model.PrintRequestCharge;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.AuswertungRepository;
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
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
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

    @Inject
    private AuswertungRepository auswertungRepository;

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

        logger.warn("Printing cancelled.");
        return Response.ok().build();
    }

    @POST
    @Path("/print/all")
    @Produces(APPLICATION_JSON)
    public synchronized Response printAll(final PrintRequest req) throws Exception {
        try {
            if(running.get()<=0) {
                for(PrintRequestCharge charge : req.getCharges()) {
                    if(charge.getOrders()!=null && !charge.getOrders().isEmpty()) {
                        Map<String, Object> params = new HashMap<>();
                        params.put("_charge", charge.getPos());
                        params.put("_order_ids", charge.getOrders());
                        print(new PrintJob(DocumentType.REPORT.name(), null, params, req.getChargeSize()));

                        for(String orderId : charge.getOrders()) {
                            Future<Boolean> invoiceSuccess = print(new PrintJob(DocumentType.INVOICE.name(), orderId, req.getChargeSize()));
                            Future<Boolean> labelSuccess = print(new PrintJob(DocumentType.LABEL.name(), orderId, req.getChargeSize()));

                            // TODO: check if this doesn't block
                            if(invoiceSuccess.get() && labelSuccess.get()) {
                                auswertungRepository.timestampPrint(orderId);
                            }
                        }
                    }
                }
            } else {
                logger.warn("Please cancel first all running print jobs.");
            }
        } catch (Throwable e) {
            if(e instanceof InterruptedException) {
                logger.warn("Print job cancelled.");
            } else {
                // TODO: store this in local variable
                logger.error(e.toString(), e);
            }
        }

        return Response.ok().build();
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

                    if (documentType.equals(DocumentType.INVOICE) || documentType.equals(DocumentType.LABEL)) {
                        print(documentType, printer, dir + "/" + req.getOrderId() + "/" + req.getType().toLowerCase() + ".pdf", fast);
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

    public static <T> List<T> slice(List<T> list, int index, int count) {
        List<T> result = new ArrayList<>();
        if (index >= 0 && index < list.size()) {
            int end = index + count < list.size() ? index + count : list.size();
            for (int i = index; i < end; i++) {
                result.add(list.get(i));
            }
        }
        return result;
    }
}
