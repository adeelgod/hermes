package com.m11n.hermes.rest.api;

import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeBodyPart;
import com.sun.xml.internal.messaging.saaj.packaging.mime.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

@Path("/configuration")
@Produces(APPLICATION_JSON)
public class ConfigurationResource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream(getPropertyFile()));
        return Response.ok(p).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(Map<String, String> properties) throws Exception {
        Properties p = new Properties();

        for(Map.Entry<String, String> entry : properties.entrySet()) {
            p.setProperty(entry.getKey(), entry.getValue());
        }

        p.store(new FileOutputStream("hermes.properties"), "Test");

        return Response.ok().build();
    }

    @POST
    @Path("/templates")
    @Produces(APPLICATION_JSON)
    public Response upload(MimeMultipart entity) throws Exception {
        // TODO: implement this
        logger.info("########################### CONFIG UPLOAD...");

        for(int i=0; i<entity.getCount(); i++) {
            MimeBodyPart part = entity.getBodyPart(i);

            logger.info("########################### CONFIG UPLOAD: {} ({})", part.getContentID(), part.getContentType());
        }

        return Response.ok().build();
    }

    private String getPropertyFile() {
        String file = System.getProperty("hermes.config");

        if(file==null) {
            file = "hermes.properties";
        } else {
            file = file.replace("file:", "");
        }

        return file;
    }
}
