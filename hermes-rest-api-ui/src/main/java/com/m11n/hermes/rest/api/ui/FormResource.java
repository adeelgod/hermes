package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.persistence.FormRepository;
import com.m11n.hermes.persistence.IntrashipDocumentRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import com.m11n.hermes.persistence.util.QueryToFieldsUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Path("/forms")
@Produces(MediaType.APPLICATION_JSON)
@Controller
public class FormResource {
    private static final Logger logger = LoggerFactory.getLogger(FormResource.class);

    @Inject
    private SshService sshService;

    @Inject
    private QueryScheduler queryScheduler;

    @Inject
    private FormRepository formRepository;

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Inject
    private QueryToFieldsUtil queryToFieldsUtil;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @Value("${hermes.server.result.dir}")
    private String serverResultDir;

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(formRepository.findAll()).cacheControl(cc).build();
    }

    @POST
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(Map<String, Object> parameters) {
        Object result = Collections.emptyList();
        try {
            result = queryScheduler.query(parameters);
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }

        if(result instanceof List) {
            List<Map<String, Object>> r = (List<Map<String, Object>>)result;

            final boolean downloadFiles = parameters.get("_downloadFiles")==null ? false : (Boolean)parameters.get("_downloadFiles");

            try {
                sshService.connect();

                for(Map<String, Object> row : r) {
                    if(Boolean.FALSE.equals(row.get("_labelExists")) && downloadFiles) {
                        String orderId = row.get("orderId").toString();
                        String shippingId = row.get("shippingId").toString();

                        if(row.get("_labelPath")!=null) {
                            String path = row.get("_labelPath").toString();

                            try {
                                /**
                                File f = new File(resultDir + "/" + row.get("orderId"));
                                if(!f.exists()) {
                                    f.mkdirs();
                                }
                                logger.info("COPY: {} -> {}", path, resultDir + "/" + orderId + "/label.pdf");
                                sshService.copy(path, resultDir + "/" + orderId + "/label.pdf");
                                 */
                                logger.info("mkdir -p " + serverResultDir + "/" + orderId + " && cp " + path + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                int status = sshService.exec("mkdir -p " + serverResultDir + "/" + orderId + " && cp " + path + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                row.put("_labelExists", status==0);
                            } catch (Exception e) {
                                logger.error(e.toString(), e);
                            }
                        } else {
                            logger.info("FILE NOT FOUND: {} -> {}", orderId, shippingId);
                        }
                    }
                }

                sshService.disconnect();
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }

        return Response.ok(result).build();
    }

    @POST
    @Path("download")
    @Produces(MediaType.APPLICATION_JSON)
    public Response download(final List<Map<String, Object>> items) {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sshService.connect();

                    for(Map<String, Object> item : items) {
                        String orderId = item.get("orderId")==null ? null : item.get("orderId").toString();
                        String shippingId = item.get("shippingId")==null ? null : item.get("shippingId").toString();

                        if(!StringUtils.isEmpty(orderId) && !StringUtils.isEmpty(shippingId)) {
                            logger.debug("DOWNLOAD: {} - {}", orderId, shippingId);

                            boolean labelExists = new File(resultDir + "/" + orderId + "/label.pdf").exists();

                            if(!labelExists) {
                                String labelPath = intrashipDocumentRepository.findFilePath(shippingId);

                                try {
                                    /**
                                    File f = new File(resultDir + "/" + orderId);
                                    if (!f.exists()) {
                                        f.mkdirs();
                                    }
                                    logger.debug("#################### COPY: {} -> {}", labelPath, resultDir + "/" + orderId + "/label.pdf");
                                    sshService.copy(labelPath, resultDir + "/" + orderId + "/label.pdf");
                                     */
                                    logger.info("mkdir -p " + serverResultDir + "/" + orderId + " && cp " + labelPath + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                    sshService.exec("mkdir -p " + serverResultDir + "/" + orderId + " && cp " + labelPath + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                } catch (Exception e) {
                                    logger.error(e.toString(), e);
                                }
                            }
                        }
                    }

                    sshService.disconnect();
                } catch (Exception e) {
                    logger.error(e.toString(), e);
                }
            }
        });

        return Response.ok().build();
    }

    @GET
    @Path("{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("name") String name) {
        return Response.ok(formRepository.findByName(name)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Form form) {
        if(form.getId()==null && (form.getFields()==null || form.getFields().isEmpty())) {
            List<FormField> fields = queryToFieldsUtil.toFields(form.getDb(), form.getSqlStatement());

            if (!fields.isEmpty()) {
                form.setFields(fields);
            }
        }

        return Response.ok(formRepository.save(form)).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@QueryParam("id") String id) {
        formRepository.delete(id);
        return Response.ok().build();
    }
}
