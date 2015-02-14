package com.m11n.hermes.rest.server.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/ping")
@Produces(APPLICATION_JSON)
public class PingResource {

    @GET
    public Response ping() {
        return Response.ok("pong").build();
    }
}
