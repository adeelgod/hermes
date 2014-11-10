package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.DocumentType;
import com.m11n.hermes.core.service.PrinterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import java.io.FileInputStream;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/printers")
@Produces(APPLICATION_JSON)
public class PrinterResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterResource.class);

    @Inject
    private PrinterService printerService;

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

    @GET
    @Path("/print")
    @Produces(APPLICATION_JSON)
    public Response print(@QueryParam("type") String type, @QueryParam("orderId") String orderId) throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream(getPropertyFile()));

        String dir = p.getProperty("hermes.result.dir");

        DocumentType documentType = DocumentType.valueOf(type);

        String printer = null;

        if(documentType.equals(DocumentType.INVOICE)) {
            printer = p.getProperty("hermes.printer.invoice");
        } else if(documentType.equals(DocumentType.LABEL)) {
            printer = p.getProperty("hermes.printer.label");
        } else if(documentType.equals(DocumentType.REPORT)) {
            printer = p.getProperty("hermes.printer.report");
        }

        logger.info("******************************************** PRINT: printer:{} type:{} order:{}", printer, type, orderId);

        if(documentType.equals(DocumentType.INVOICE) || documentType.equals(DocumentType.LABEL)) {
            PrinterService.JobStatus status = printerService.print(dir + "/" + orderId + "/" + type.toLowerCase() + ".pdf", printer);

            logger.info("******************************************** PRINT: {} ({})", dir + "/" + orderId + "/" + type.toLowerCase() + ".pdf", status);

            return Response.ok().build();
        } else if(documentType.equals(DocumentType.REPORT)) {
            // TODO: implement this
        }

        return Response.serverError().build();
    }

    private String getPropertyFile() {
        String file = System.getProperty("hermes.config");

        if(file==null) {
            file = "hermes.properties";
        } else {
            file = file.replace("file:", "");
        }

        return file;
    }
}
