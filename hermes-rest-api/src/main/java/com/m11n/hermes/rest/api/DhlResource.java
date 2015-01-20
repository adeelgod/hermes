package com.m11n.hermes.rest.api;

import com.m11n.hermes.service.dhl.JerryDhlService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Singleton
@Path("/dhl")
@Produces(APPLICATION_JSON)
public class DhlResource {
    //private static final Logger logger = LoggerFactory.getLogger(DhlResource.class);

    @Inject
    private JerryDhlService dhlService;

    @GET
    @Path("/tracking/status")
    @Produces(APPLICATION_JSON)
    public Response track(@QueryParam("code") String code) {
        String result = dhlService.getTrackingStatus(code);

        Map<String, String> status = new HashMap<>();
        status.put("status", "unknown");
        status.put("description", result);

        return Response.status(Response.Status.OK).entity(status).build();
    }
}
