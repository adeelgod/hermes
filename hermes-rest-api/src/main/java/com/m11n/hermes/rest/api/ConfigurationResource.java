package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.persistence.FormRepository;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.*;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

@Path("/configuration")
@Produces(APPLICATION_JSON)
public class ConfigurationResource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationResource.class);

    @Inject
    private ReportService reportService;

    @Inject
    private FormRepository formRepository;

    @Inject
    private PrinterService printerService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        Properties p = new Properties();
        p.load(new FileInputStream(getPropertyFile()));

        Map<String, Object> configuration = new HashMap<>();

        configuration.put("properties", p);
        configuration.put("forms", formRepository.findAll());
        configuration.put("printers", printerService.printers());

        File templateDir = new File(reportService.getTemplateDir());

        String[] templates = null;

        if(templateDir.exists()) {
            templates = templateDir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jrxml");
                }
            });
        }

        configuration.put("templates", templates);

        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(configuration).cacheControl(cc).build();
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
    @Consumes(MULTIPART_FORM_DATA)
    @Produces(APPLICATION_JSON)
    //public Response upload(@FormDataParam("file") InputStream is, @FormDataParam("file") FormDataContentDisposition detail) throws Exception {
    public Response upload(FormDataMultiPart formParams) throws Exception {

        // TODO: implement this

        for(Map.Entry<String, List<FormDataBodyPart>> entry : formParams.getFields().entrySet()) {
            //FormDataBodyPart field = entry.getValue();
            for(FormDataBodyPart field : entry.getValue()) {
                logger.info("########################### CONFIG UPLOAD... {} - {} ({})", entry.getKey(), field.getName(), field.getMediaType());
            }
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
