package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.core.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Path("/util")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class UtilResource {
    private static final Logger logger = LoggerFactory.getLogger(UtilResource.class);

    @GET
    @Path("/netdrive")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queue() throws Exception {
        Properties p = PropertiesUtil.getProperties();
        String command = p.getProperty("hermes.netdrive.command");

        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor(30, TimeUnit.SECONDS);

        /**
        StringWriter writer = new StringWriter();
        IOUtils.copy(proc.getInputStream(), writer);
        logger.info("Netdrive command executed: {}", writer);
         */

        logger.info("Netdrive command executed: {}", proc.exitValue());

        return Response.ok().build();
    }
}
