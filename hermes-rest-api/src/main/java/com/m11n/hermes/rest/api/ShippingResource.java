package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.LabelStatus;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.LabelStatusRepository;
import com.m11n.hermes.persistence.SalesFlatShipmentCommentRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/shipping")
@Produces(APPLICATION_JSON)
@Controller
public class ShippingResource {
    private static final Logger logger = LoggerFactory.getLogger(ShippingResource.class);

    @Inject
    private QueryScheduler queryScheduler;

    @Inject
    private LabelStatusRepository labelStatusRepository;

    @Inject
    private SalesFlatShipmentCommentRepository salesFlatShipmentCommentRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

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
        // TODO: implement this
        String message = magentoService.createIntrashipLabel(orderId);
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);
        result.put("status", "info"); // TODO: correct this
        result.put("message", message);
        return Response.ok(result).build();
    }

    @GET
    @Path("/status")
    @Produces(APPLICATION_JSON)
    public Response status(@QueryParam("orderId") String orderId) {
        // TODO: check this
        String shippingId = auswertungRepository.findShippingIdByOrderId(orderId);
        List<String> rawStatuses = salesFlatShipmentCommentRepository.findRawStatus(shippingId);
        List<LabelStatus> statuses = new ArrayList<>();

        for(String rawStatus : rawStatuses) {
            statuses.add(labelStatusRepository.findByText(rawStatus));
        }

        return Response.ok(statuses).build();
    }
}
