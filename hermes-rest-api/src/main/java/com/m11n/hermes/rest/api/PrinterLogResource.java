package com.m11n.hermes.rest.api;

import com.m11n.hermes.persistence.PrinterLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/printers/logs")
@Produces(APPLICATION_JSON)
public class PrinterLogResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterLogResource.class);

    @Inject
    private PrinterLogRepository printerLogRepository;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("size") @DefaultValue("20") int size) throws Exception {
        return Response.ok(printerLogRepository.findAll(new PageRequest(page, size))).build();
    }
}
