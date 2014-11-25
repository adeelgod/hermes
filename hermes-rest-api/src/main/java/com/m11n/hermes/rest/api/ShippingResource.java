package com.m11n.hermes.rest.api;

import com.m11n.hermes.persistence.LabelStatusRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import com.m11n.hermes.service.magento.MagentoServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

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

    private String username;

    private String password;

    @PostConstruct
    public void init() {
        MagentoServiceLocator locator = new MagentoServiceLocator();
        // TODO: set username and password
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
    public Response status() {
        // TODO: implement this
        return Response.ok().build();
    }
}
