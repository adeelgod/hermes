package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/fake/shipping")
@Produces(APPLICATION_JSON)
@Controller
public class FakeShippingResource {
    private static final Logger logger = LoggerFactory.getLogger(FakeShippingResource.class);

    private AtomicInteger shipmentCount = new AtomicInteger();

    private AtomicInteger labelCount = new AtomicInteger();

    @GET
    @Path("/shipment")
    @Produces(APPLICATION_JSON)
    public Response createShipment(@QueryParam("orderId") String orderId) throws Exception {
        logger.debug("Faking shipment... #{}", shipmentCount.incrementAndGet());
        Thread.sleep(2000L);

        return Response.ok(Collections.singletonMap("shipmentId", new Date().getTime())).build();
    }

    @GET
    @Path("/label")
    @Produces(APPLICATION_JSON)
    public Response createLabel(@QueryParam("orderId") String orderId) throws Exception {
        logger.debug("Faking label... #{}", labelCount.incrementAndGet());
        Thread.sleep(2000L);

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "success");
        result.put("message", "DHL Intraship::pdf::0::PDF creation was successful");
        return Response.ok(result).build();
    }
}
