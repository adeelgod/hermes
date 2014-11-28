package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/reports")
@Produces(APPLICATION_JSON)
public class ReportResource {
    private static final Logger logger = LoggerFactory.getLogger(ReportResource.class);

    @Inject
    private ReportService reportService;

    @Inject
    private FormRepository formRepository;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

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

        return Response.ok(templates).cacheControl(cc).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response report(final Map<String, Object> parameters) throws Exception {
        final Form form = formRepository.findByName(parameters.get("_form").toString());
        parameters.remove("_form");

        for(FormField field : form.getFields()) {
            // TODO: what's up with TIME?
            if(field.getFieldType().equals(FormField.Type.DATE.name()) || field.getFieldType().equals(FormField.Type.DATETIME.name())) {
                String value = parameters.get(field.getName()).toString();
                DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(value);
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                parameters.put(field.getName(), df.print(dt));
            }
        }

        String id = form.getName() + "-" + UUID.randomUUID().toString();

        File f = new File(resultDir + "/reports");

        if(!f.exists()) {
            f.mkdirs();
        }

        reportService.generate(form, parameters, new FileOutputStream(resultDir + "/reports/" + id + ".pdf"));

        return Response.ok(Collections.singletonMap("id", id)).build();
    }


    @GET
    @Path("pdf/{id}")
    @Produces({"application/pdf"})
    public StreamingOutput pdf(@PathParam("id") final String id) throws Exception {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                IOUtils.copy(new FileInputStream(resultDir + "/reports/" + id + ".pdf"), os);
            }
        };
    }

    /**
     * TODO: make this work!
    @POST
    @Produces({"application/pdf"})
    public StreamingOutput report(final Map<String, Object> parameters) throws Exception {
        final Form form = formRepository.findByName(parameters.get("_form").toString());
        parameters.remove("_form");

        for(FormField field : form.getFields()) {
            // TODO: what's up with TIME?
            if(field.getFieldType().equals(FormField.Type.DATE.name()) || field.getFieldType().equals(FormField.Type.DATETIME.name())) {
                String value = parameters.get(field.getName()).toString();
                DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(value);
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                parameters.put(field.getName(), df.print(dt));
            }
        }

        logger.debug("+++++++++++++++++++++ REPORT: {}", parameters);

        //reportService.generate(form, parameters, new FileOutputStream("target/report-" + UUID.randomUUID().toString() + ".pdf"));

        return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException, WebApplicationException {
                reportService.generate(form, parameters, os);
            }
        };
    }
    */
}
