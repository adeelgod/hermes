package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.service.ReportService;
import com.m11n.hermes.persistence.PrinterLogRepository;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import java.io.File;
import java.io.FilenameFilter;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/reports")
@Produces(APPLICATION_JSON)
public class ReportResource {
    private static final Logger logger = LoggerFactory.getLogger(ReportResource.class);

    @Inject
    private ReportService reportService;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() throws Exception {
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

        return Response.ok(templates).build();
    }
}