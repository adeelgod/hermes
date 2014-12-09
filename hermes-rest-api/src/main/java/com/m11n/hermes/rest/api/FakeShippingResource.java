package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.*;
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
        List<Map<String, Object>> stati = new ArrayList<>();

        logger.debug("Faking label... #{}", labelCount.incrementAndGet());

        Thread.sleep(2000L);

        // NOTE: simulating more complex round-trip
        stati.add(createIntrashipResponse(orderId, "retry", "DHL Intraship::create::-2::Unable to save PDF to /home/l-carb-shop.de/public_html/var/intraship/documents/pdf--4/pdf--40/pdf--402/label-00340433836281813654.pdf.", 1));
        stati.add(createIntrashipResponse(orderId, "retry", "DHL Intraship::create::0::Could not connect to host", 2));
        stati.add(createIntrashipResponse(orderId, "success", "DHL Intraship::pdf::0::PDF creation was successful", 3));

        return Response.ok(stati).build();
    }

    private Map<String, Object> createIntrashipResponse(String orderId, String status, String message, int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", status);
        response.put("message", message);
        response.put("count", count);

        return response;
    }
}
