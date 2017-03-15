package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.service.dhl.DefaultDhlService;
import com.m11n.hermes.service.dhl.JerryDhlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Singleton
@Path("/dhl")
@Produces(MediaType.APPLICATION_JSON)
public class DhlResource {
    private static final Logger logger = LoggerFactory.getLogger(DhlResource.class);

//       we can not rely on html parser technique.
//    @Inject
//    private JerryDhlService dhlService;

    //    DHL api integrated class
    @Inject
    private DefaultDhlService defaultDhlService;

    @GET
    @Path("/tracking/check/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status() {
        return Response.ok(defaultDhlService.trackingCheckStatus()).build();
        // html parser technique is no more useful
//        return Response.ok(dhlService.trackingCheckStatus()).build();
    }

    @GET
    @Path("/tracking/check")
    @Produces(MediaType.APPLICATION_JSON)
    public Response check() {
        try {
            // html parser technique is no more useful
//            dhlService.checkTracking();
            defaultDhlService.checkTracking();
        } catch (Exception ex) {
            logger.error("ERROR : ", ex);
        }
        return Response.ok().build();
    }

    @GET
    @Path("/tracking/check/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response cancel() throws Exception {
        // html parser technique is no more useful
//        dhlService.cancelTracking();
        defaultDhlService.cancelTracking();
        return Response.ok().build();
    }

    @GET
    @Path("/tracking/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response track(@QueryParam("code") String code) {
        return Response.status(Response.Status.OK).entity(defaultDhlService.getTrackingStatus(code)).build();
        // html parser technique is no more useful
//        return Response.status(Response.Status.OK).entity(dhlService.getTrackingStatus(code)).build();
    }
}
