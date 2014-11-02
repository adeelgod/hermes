package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/printers/logs")
@Produces(APPLICATION_JSON)
public class PrinterLogResource {
    private static final Logger logger = LoggerFactory.getLogger(PrinterLogResource.class);

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        // TODO: implement this

        return Response.ok().build();
    }
}
