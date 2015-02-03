package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.service.PrinterService;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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

    @Inject
    private StringEncryptor encryptor;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        Properties p = PropertiesUtil.getProperties();

        migrate(p);

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

    private void migrate(Properties p) {
        // NOTE: fill in defaults for new properties
        String[] migrations = new String[]{
                "hermes.magento.api.retry.max", "3",
                "hermes.magento.api.retry.wait", "500",
                "hermes.ssh.compression", "3"
        };
        for(int i=0; i<migrations.length; i+=2) {
            String key = migrations[i];
            String value = migrations[i+1];
            if(StringUtils.isEmpty(p.getProperty(key))) {
                p.setProperty(key, value);
            }
        }
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(Map<String, String> properties) throws Exception {
        Properties p = new Properties();

        for(Map.Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if(key.contains("password")) {
                value = encrypt(value);
            } else if(key.contains("dir") && value.contains("\\")) {
                // reduce pairs to singles
                value = value.replaceAll("\\\\\\\\", "\\\\");
                // replace all crap with forward slash
                value = value.replaceAll("\\\\", "/");
            }

            p.setProperty(key, value);
        }

        //p.store(new FileOutputStream("hermes.properties"), "Hermes");
        PropertiesUtil.save(p);

        return Response.ok().build();
    }

    private String encrypt(String value) {
        if(value.startsWith("ENC(") || StringUtils.isEmpty(value)) {
            //logger.info("+++++++++++++++++++++++ SKIP ENCRYPTION: {} -> {}", name, value);
            return value;
        } else {
            String val = "ENC(" + encryptor.encrypt(value) + ")";
            //logger.info("+++++++++++++++++++++++ ENCRYPTION: {} -> {} -> {}", name, value, val);
            return val;
        }
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
}
