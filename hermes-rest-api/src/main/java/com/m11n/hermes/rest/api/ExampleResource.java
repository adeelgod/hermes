package com.m11n.hermes.rest.api;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/examples")
@Produces(APPLICATION_JSON)
public class ExampleResource {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);

    @Value("${hermes.examples.dir}")
    private String examplesDir;

    @Value("${hermes.inbox.dir}")
    private String inboxDir;

    @GET
    @Path("/queue")
    @Produces(APPLICATION_JSON)
    public Response queue() throws Exception {
        //copyStream("labels.pdf", inboxDir + "/labels.pdf");
        //copyStream("invoice.pdf", inboxDir + "/invoice.pdf");

        copyFile(examplesDir + "/invoice.pdf", inboxDir);
        copyFile(examplesDir + "/examples/labels.pdf", inboxDir);

        return Response.ok().build();
    }

    private synchronized void copyFile(String source, String target) throws Exception {
        lock(target);
        FileUtils.copyFileToDirectory(new File(source), new File(target));
        unlock(target);
    }

    // NOTE: produces corrupted PDFs; not usuable for the moment
    private synchronized void copyStream(String source, String target) throws Exception {
        lock(target);
        IOUtils.copy(ExampleResource.class.getClassLoader().getResourceAsStream(source), new FileOutputStream(target));
        unlock(target);
    }

    private void lock(String file) throws Exception {
        new File(file + ".camelLock").createNewFile();
    }

    private void unlock(String file) throws Exception {
        new File(file + ".camelLock").delete();
    }
}
