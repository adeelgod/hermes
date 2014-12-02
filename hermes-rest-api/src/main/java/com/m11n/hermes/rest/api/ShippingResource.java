package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.LabelStatus;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.LabelStatusRepository;
import com.m11n.hermes.persistence.SalesFlatShipmentCommentRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import com.m11n.hermes.service.magento.Mage_Api_Model_Server_V2_HandlerPortType;
import com.m11n.hermes.service.magento.MagentoServiceLocator;
import com.m11n.hermes.service.magento.SalesOrderEntity;
import com.m11n.hermes.service.magento.SalesOrderShipmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Value("${hermes.magento.api.username}")
    private String username;

    @Value("${hermes.magento.api.password}")
    private String apiKey;

    private String sessionId;

    private Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    @PostConstruct
    public void init() throws Exception {
        MagentoServiceLocator locator = new MagentoServiceLocator();
        magentoService = locator.getMage_Api_Model_Server_V2_HandlerPort();
    }

    private void checkSession() throws Exception {
        try {
            if(sessionId==null) {
                logger.info("New Magento session. Logging in...");
                sessionId = magentoService.login(username, apiKey);
            } else {
                logger.debug("Checking session...");
                magentoService.resources(sessionId);
            }
        } catch (Exception e) {
            logger.warn("Possible Magento session timeout. Trying to login again.");
            magentoService.login(username, apiKey);
        }
    }

    @GET
    @Path("/test")
    @Produces(APPLICATION_JSON)
    public Response test(@QueryParam("shippingId") String shippingId) throws Exception {
        checkSession();
        SalesOrderShipmentEntity shipment = magentoService.salesOrderShipmentInfo(sessionId, shippingId);

        Map<String, Object> result = new HashMap<>();
        result.put("active", shipment.getIs_active());
        result.put("created", shipment.getCreated_at());
        result.put("orderId", shipment.getOrder_increment_id());
        result.put("firstname", shipment.getShipping_firstname());
        result.put("lastname", shipment.getShipping_lastname());
        result.put("quantity", shipment.getTotal_qty());

        return Response.ok(result).build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response shipping() {
        // TODO: implement this
        return Response.ok().build();
    }

    @GET
    @Path("/label")
    @Produces(APPLICATION_JSON)
    public Response label() {
        // TODO: implement this
        return Response.ok().build();
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
