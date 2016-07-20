package com.m11n.hermes.rest.api.ui;

import com.google.common.net.HttpHeaders;
import com.m11n.hermes.core.model.DocumentsDocuments;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.service.DocumentsService;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.persistence.DocumentsDocumentsRepository;
import com.m11n.hermes.persistence.FormRepository;
import com.m11n.hermes.persistence.IntrashipDocumentRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import com.m11n.hermes.persistence.util.QueryToFieldsUtil;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.HttpUrl.Builder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private DocumentsService documentsService;
    
    @Inject
    private QueryScheduler queryScheduler;

    @Inject
    private FormRepository formRepository;
    
    @Inject
    private DocumentsDocumentsRepository documentsDocumentsRepository;

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Inject
    private QueryToFieldsUtil queryToFieldsUtil;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @Value("${hermes.server.result.dir}")
    private String serverResultDir;

    @Value("${hermes.remote.enabled:false}")
    private boolean remoteEnabled;

    @Value("${hermes.invoice.api.url}")
    protected String url;

    @Value("${hermes.invoice.api.username}")
    protected String username;

    @Value("${hermes.invoice.api.password}")
    protected String password;

    private ExecutorService executor = Executors.newFixedThreadPool(1);

    protected OkHttpClient client = new OkHttpClient();

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

            if(downloadFiles) {
            	try {
            		List<String> orderIdsLabel = new LinkedList<>();
            		List<String> orderIdsInvoice = new LinkedList<>();
            		List<String> invoiceIds = new LinkedList<>();
            		List<String> labelPaths = new LinkedList<>();
            		for(Map<String, Object> row : r) {
            			logger.debug(row.toString());
        				String orderId = row.get("orderId").toString();
        				String shippingId = row.get("shippingId").toString();
        				String invoiceId = row.get("invoiceId").toString();
        				if(Boolean.FALSE.equals(row.get("_labelExists"))) {
        					String labelPath = null;
        					if (row.get("_labelPath") != null) {
        						labelPath = row.get("_labelPath").toString();
        						orderIdsLabel.add(orderId);
        						labelPaths.add(labelPath);
        					}
        				}
        				
        				if(Boolean.FALSE.equals(row.get("_invoiceExists"))) {
        					orderIdsInvoice.add(orderId);
        					invoiceIds.add(invoiceId);
        				}
            		}
            		
            		Set<String> labelExists = documentsService.getLabels(orderIdsLabel, labelPaths);
            		Set<String> invoiceExists = documentsService.getInvoices(orderIdsInvoice, invoiceIds);
            		logger.debug("label exists: {}", labelExists);
            		logger.debug("invoice exists: {}", invoiceExists);
            		for(Map<String, Object> row : r) {
            			String orderId = row.get("orderId").toString();
            			if(labelExists.contains(orderId)) {
            				row.put("_labelExists", labelExists);
            			}
            			if(invoiceExists.contains(orderId)) {
            				row.put("_invoiceExists", labelExists);
            			}
            		}
            	} catch (Exception e) {
            		logger.error(e.toString(), e);
            	}
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
                                    if(remoteEnabled) {
                                        logger.info("mkdir -p " + serverResultDir + "/" + orderId + " && chmod 774  " + serverResultDir + "/" + orderId + " -R && cp " + labelPath + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                        sshService.exec("mkdir -p " + serverResultDir + "/" + orderId + " && chmod 774 " + serverResultDir + "/" + orderId + " -R && cp " + labelPath + " " + serverResultDir + "/" + orderId + "/label.pdf");
                                    } else {
                                        File f = new File(resultDir + "/" + orderId);
                                        if (!f.exists()) {
                                            f.mkdirs();
                                        }
                                        logger.debug("#################### COPY: {} -> {}", labelPath, resultDir + "/" + orderId + "/label.pdf");
                                        sshService.copy(labelPath, resultDir + "/" + orderId + "/label.pdf");
                                    }
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
