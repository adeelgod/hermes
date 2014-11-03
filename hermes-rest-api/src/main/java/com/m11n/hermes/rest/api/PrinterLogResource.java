package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.PrinterLog;
import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.persistence.PrinterLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/printers/logs")
@Produces(APPLICATION_JSON)
public class PrinterLogResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterLogResource.class);

    @Inject
    private PrinterLogRepository printerLogRepository;

    @Inject
    private PrinterService printerService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list(@QueryParam("from") Long from, @QueryParam("until") Long until, @QueryParam("page") @DefaultValue("0") Integer page, @QueryParam("size") @DefaultValue("20") Integer size) throws Exception {
        logger.info("############ FROM: {}", new Date(from));
        logger.info("############ UNTIL: {}", new Date(until));
        return Response.ok(printerLogRepository.findAll(new PageRequest(page, size))).build();
    }

    @GET
    @Path("/select")
    @Produces(APPLICATION_JSON)
    public Response select(@QueryParam("from") Long from, @QueryParam("until") Long until, @QueryParam("selected") Boolean selected) throws Exception {
        List<PrinterLog> result = printerLogRepository.findByProcessedAtBetween(new Date(from), new Date(until));

        for(PrinterLog printerLog : result) {
            //logger.info("############ SELECT: {}", printerLog);
            printerLog.setSelected(selected);
            printerLogRepository.save(printerLog);
        }

        return Response.ok(result.size()).build();
    }

    @GET
    @Path("/print")
    @Produces(APPLICATION_JSON)
    public Response print() throws Exception {
        printerService.printSelected();

        return Response.ok().build();
    }
}
