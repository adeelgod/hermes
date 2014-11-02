package com.m11n.hermes.rest.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/configuration")
@Produces(APPLICATION_JSON)
public class ConfigurationResource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        // TODO: implement this
        String file = System.getProperty("hermes.config");

        if(file==null) {
            file = "hermes.properties";
        } else {
            file = file.replace("file:", "");
        }

        Properties p = new Properties();
        p.load(new FileInputStream(file));
        return Response.ok(p).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save() throws Exception {
        // TODO: implement this

        return Response.ok().build();
    }
}
