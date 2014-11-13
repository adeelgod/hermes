package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

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

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(printerService.printers()).cacheControl(cc).build();
    }

    @GET
    @Path("/{name}/status")
    @Produces(APPLICATION_JSON)
    public Response status(@PathParam("name") String name) {
        return Response.ok(printerService.status(name)).build();
    }

    @POST
    @Path("/print")
    @Produces(APPLICATION_JSON)
    public synchronized Response print(Map<String, Object> params) {
        try {
            Properties p = PropertiesUtil.getProperties();

            String dir = p.getProperty("hermes.result.dir");

            String type = params.get("type").toString();

            DocumentType documentType = DocumentType.valueOf(type);

            String printer = null;

            if(documentType.equals(DocumentType.INVOICE)) {
                printer = p.getProperty("hermes.printer.invoice");
            } else if(documentType.equals(DocumentType.LABEL)) {
                printer = p.getProperty("hermes.printer.label");
            } else if(documentType.equals(DocumentType.REPORT)) {
                printer = p.getProperty("hermes.printer.report");
            }

            logger.info("******************************************** PRINT: {}", this);

            if(documentType.equals(DocumentType.INVOICE) || documentType.equals(DocumentType.LABEL)) {
                String orderId = params.get("orderId").toString();

                logger.info("******************************************** PRINT: printer:{} type:{} order:{}", printer, type, orderId);

                PrinterService.JobStatus status = printerService.print(dir + "/" + orderId + "/" + type.toLowerCase() + ".pdf", printer);

                logger.info("******************************************** PRINT: {} ({})", dir + "/" + orderId + "/" + type.toLowerCase() + ".pdf", status);

                Thread.sleep(1000); // TODO: make this configurable

                logger.info("******************************************** PRINT: wakeup...");

                return Response.ok().build();
            } else if(documentType.equals(DocumentType.REPORT)) {
                logger.info("******************************************** PRINT: REPORT ORDER_IDS {} ({})", params.get("_order_ids"), params.get("_order_ids").getClass().getName());

                String[] templates = params.get("_templates").toString().split("\\|");

                for(String template : templates) {
                    String reportOutput = dir + "/reports/" + UUID.randomUUID().toString() + ".pdf";

                    reportService.generate(template, params, "pdf", reportOutput);

                    PrinterService.JobStatus status = printerService.print(reportOutput, printer);

                    logger.info("******************************************** PRINT: {} ({})", reportOutput, status);

                    Thread.sleep(1000); // TODO: make this configurable

                    logger.info("******************************************** PRINT: wakeup...");

                    // cleanup
                    FileUtils.deleteQuietly(new File(reportOutput));
                }

                return Response.ok().build();
            }
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }

        //return Response.serverError().build();
        // TODO: return some status to check!
        return Response.ok().build();
    }
}
