package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.service.PdfService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/printers")
@Produces(APPLICATION_JSON)
public class PrinterResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterResource.class);

    @Inject
    private PdfService pdfService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        return Response.ok(pdfService.printers()).build();
    }

    @GET
    @Path("/{name}/status")
    @Produces(APPLICATION_JSON)
    public Response status(@PathParam("name") String name) {
        return Response.ok(pdfService.status(name)).build();
    }
}
