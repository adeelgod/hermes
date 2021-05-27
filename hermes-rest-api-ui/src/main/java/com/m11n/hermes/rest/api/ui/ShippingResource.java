package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.core.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;

@Path("/shipping")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class ShippingResource {
    private static final Logger logger = LoggerFactory.getLogger(ShippingResource.class);

    @Inject
    private MagentoService magentoService;

    @Inject
    private SshService sshService;

    @GET
    @Path("/shipment")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createShipment(@QueryParam("target") String target, @QueryParam("orderId") String orderId) throws Exception {
        //return Response.ok(Collections.singletonMap("shipmentId", magentoService.createShipment(target, orderId))).build();
        Properties config = PropertiesUtil.getProperties();
        String warnMsg = config.getProperty("hermes.shipment.status.warning");

        String result = magentoService.createShipment(target, orderId);

        JSONObject jsonObject = new JSONObject(result);
        String strResult = (String)jsonObject.get("result");
        String strMessage = (String)jsonObject.get("message");

        //logger.debug("Shipment WarnMsg : " + warnMsg);

        if(strResult.equals("error") && !strMessage.equals(warnMsg)) {
			logger.debug("********* Shipment Error: {}", strMessage); 
            return Response.serverError()
                    .status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
        }

		if(strMessage.equals(warnMsg)) {
			logger.debug("********* Shipment Warning: {}", strMessage); 
		}else{
			logger.debug("********* Shipment is created successfully."); 
		}
		
        return Response.ok(result).build();
    }

    /*@GET
    @Path("/label")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLabel(@QueryParam("target") String target, @QueryParam("orderId") String orderId) throws Exception {
        String result = magentoService.createShippingLabel(target, orderId);

        JSONObject jsonObject = new JSONObject(result);
        String strResult = (String)jsonObject.get("result");
        String strMessage = (String)jsonObject.get("message");

        if(strResult.equals("error")) {
            return Response.serverError()
                    .status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
        }

        return Response.ok(result).build();
    }*/

    @GET
    @Path("/label")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createLabel(@QueryParam("target") String target, @QueryParam("orderId") String orderId) throws Exception {
        List<Map<String, Object>> result = magentoService.createIntrashipLabel(target, orderId);

        return Response.ok(result).build();
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response status(@QueryParam("orderId") String orderId) {
        return Response.ok(magentoService.getIntrashipStatuses(orderId)).build();
    }
}
