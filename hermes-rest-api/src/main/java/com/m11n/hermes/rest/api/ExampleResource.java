package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/example")
@Produces(APPLICATION_JSON)
public class ExampleResource {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);

    @GET
    @Path("/queue")
    @Produces(APPLICATION_JSON)
    public Response queue() {
        // TODO: queue the sample files
        return Response.ok().build();
    }
}
