package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/forms")
@Produces(APPLICATION_JSON)
public class FormResource {
    private static final Logger logger = LoggerFactory.getLogger(FormResource.class);

    @Inject
    private FormRepository formRepository;

    @Inject
    private NamedParameterJdbcTemplate jdbcTemplate;

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
                result = jdbcTemplate.query(form.getSqlStatement(), parameters, new RowMapper<Map<String, Object>>() {
                    @Override
                    public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                        Map<String, Object> row = new HashMap<>();
                        ResultSetMetaData metaData = resultSet.getMetaData();

                        for(int j=1; j<=metaData.getColumnCount(); j++) {
                            Object value = null;
                            switch(metaData.getColumnType(j)) {
                                case Types.BOOLEAN:
                                    value = resultSet.getBoolean(j);
                                    break;
                                case Types.CHAR:
                                case Types.VARCHAR:
                                case Types.LONGVARCHAR:
                                    value = resultSet.getString(j);
                                    break;
                                case Types.SMALLINT:
                                case Types.TINYINT:
                                case Types.INTEGER:
                                    value = resultSet.getInt(j);
                                    break;
                                case Types.DECIMAL:
                                    value = resultSet.getDouble(j);
                                    break;
                                case Types.BIGINT:
                                    value = resultSet.getBigDecimal(j);
                                    break;
                                case Types.DATE:
                                    value = resultSet.getDate(j);
                                    break;
                            }

                            String name = metaData.getColumnLabel(j);

                            if(StringUtils.isEmpty(name)) {
                                name = metaData.getColumnName(j);
                            }

                            row.put(name, value);
                        }

                        if(row.containsKey("orderId")) {
                            row.put("_invoiceExists", new File(resultDir + "/" + row.get("orderId") + "/invoice.pdf").exists());
                            row.put("_labelExists", new File(resultDir + "/" + row.get("orderId") + "/label.pdf").exists());
                        }

                        return row;
                    }
                });
            } else {
                // TODO: implement this; maybe check insert/update
                //int count = jdbcTemplate.update(statement, parameters);
                //Map<String, Integer> r = new HashMap<>();
                //r.put("modified", count);
                //result = r;
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
