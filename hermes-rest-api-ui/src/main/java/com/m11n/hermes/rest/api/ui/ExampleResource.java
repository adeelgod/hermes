package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.core.util.PropertiesUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

@Path("/examples")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class ExampleResource {
    private static final Logger logger = LoggerFactory.getLogger(ExampleResource.class);

    @GET
    @Path("/queue")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queue() throws Exception {
        Properties p = PropertiesUtil.getProperties();
        p.getProperty("hermes.admin.password");

        String examplesDir = p.getProperty("hermes.examples.dir");
        String inboxDir = p.getProperty("hermes.inbox.dir");

        //copyStream("labels.pdf", inboxDir + "/labels.pdf");
        //copyStream("invoice.pdf", inboxDir + "/invoice.pdf");

        copyFile(examplesDir + "/invoice.pdf", inboxDir);
        copyFile(examplesDir + "/labels.pdf", inboxDir);

        return Response.ok().build();
    }

    private synchronized void copyFile(String source, String target) throws Exception {
        File sourceFile = new File(source);
        lock(target + "/" + sourceFile.getName());
        FileUtils.copyFileToDirectory(sourceFile, new File(target));
        unlock(target + "/" + sourceFile.getName());
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
