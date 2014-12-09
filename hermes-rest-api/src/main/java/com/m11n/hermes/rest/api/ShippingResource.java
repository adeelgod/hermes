package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.service.MagentoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/shipping")
@Produces(APPLICATION_JSON)
@Controller
public class ShippingResource {
    private static final Logger logger = LoggerFactory.getLogger(ShippingResource.class);

    @Inject
    private MagentoService magentoService;

    @GET
    @Path("/shipment")
    @Produces(APPLICATION_JSON)
    public Response createShipment(@QueryParam("orderId") String orderId) throws Exception {
        return Response.ok(Collections.singletonMap("shipmentId", magentoService.createShipment(orderId))).build();
    }

    @GET
    @Path("/label")
    @Produces(APPLICATION_JSON)
    public Response createLabel(@QueryParam("orderId") String orderId) throws Exception {
        Map<String, Object> result = magentoService.createIntrashipLabel(orderId);
        return Response.ok(result).build();
    }

    @GET
    @Path("/status")
    @Produces(APPLICATION_JSON)
    public Response status(@QueryParam("orderId") String orderId) {
        return Response.ok(magentoService.getIntrashipStatuses(orderId)).build();
    }
}
