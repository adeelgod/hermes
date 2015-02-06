package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/version")
@Produces(APPLICATION_JSON)
@Controller
public class VersionResource {
    private static final Logger logger = LoggerFactory.getLogger(VersionResource.class);

    @Inject
    private Version version;

    @GET
    @Produces(APPLICATION_JSON)
    public Response version() throws Exception {
        return Response.ok(version).build();
    }
}
