package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.service.SshService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BaseRowMapper;
import com.m11n.hermes.persistence.FormRepository;
import com.m11n.hermes.persistence.IntrashipDocumentRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/forms")
@Produces(APPLICATION_JSON)
@Controller
public class FormResource {
    private static final Logger logger = LoggerFactory.getLogger(FormResource.class);

    @Inject
    private SshService sshService;

    @Inject
    private FormRepository formRepository;

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(formRepository.findAll()).cacheControl(cc).build();
    }

    @POST
    @Path("query")
    @Produces(APPLICATION_JSON)
    public Response query(Map<String, Object> parameters) {
        Form form = formRepository.findByName(parameters.get("_form").toString());

        final boolean checkFiles = parameters.get("_checkFiles")==null ? false : (Boolean)parameters.get("_checkFiles");
        final boolean downloadFiles = parameters.get("_downloadFiles")==null ? false : (Boolean)parameters.get("_downloadFiles");

        for(FormField field : form.getFields()) {
            if(field.getType().equals(FormField.Type.DATE.name())) {
                String value = parameters.get(field.getName()).toString();
                DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(value);
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                parameters.put(field.getName(), df.print(dt));
            }
        }

        for(Map.Entry<String, Object> entry : parameters.entrySet()) {
            logger.info("#################### PARAM: {} = {} ({})", entry.getKey(), entry.getValue(), entry.getValue().getClass().getName());
        }

        String[] statements = form.getSqlStatement().split(";");

        //List<Map<String, Object>> result = null;
        Object result = null;

        for(String statement : statements) {
            statement = statement.trim();

            logger.info("#################### STATEMENT: {}", statement);

            if(statement.toLowerCase().startsWith("select")) {
                result = auswertungRepository.query(form.getSqlStatement(), parameters, new BaseRowMapper() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        Map<String, Object> row = new HashMap<>();
                        ResultSetMetaData metaData = resultSet.getMetaData();

                        for(int j=1; j<=metaData.getColumnCount(); j++) {
                            String name = getLabel(metaData, j);
                            Object value = getValue(resultSet, j);

                            row.put(name, value);
                        }

                        if(row.containsKey("orderId") && checkFiles) {
                            row.put("_invoiceExists", new File(resultDir + "/" + row.get("orderId") + "/invoice.pdf").exists());
                            row.put("_labelExists", new File(resultDir + "/" + row.get("orderId") + "/label.pdf").exists());

                            if(Boolean.FALSE.equals(row.get("_labelExists")) && downloadFiles) {
                                String path = intrashipDocumentRepository.findDocumentUrl(row.get("orderId").toString());
                            }
                        }

                        return row;
                    }
                });
            } else {
                result = Collections.singletonMap("modified", auswertungRepository.update(statement, parameters));
            }
        }

        return Response.ok(result).build();
    }

    @GET
    @Path("{name}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("name") String name) {
        return Response.ok(formRepository.findByName(name)).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(Form form) {
        return Response.ok(formRepository.save(form)).build();
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    public Response remove(@QueryParam("uid") String uid) {
        formRepository.delete(uid);
        return Response.ok().build();
    }
}
