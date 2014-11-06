package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.persistence.FormRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/forms")
@Produces(APPLICATION_JSON)
public class FormResource {
    private static final Logger logger = LoggerFactory.getLogger(FormResource.class);

    @Inject
    private FormRepository formRepository;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        return Response.ok(formRepository.findAll()).build();
    }

    @POST
    @Path("query")
    @Produces(APPLICATION_JSON)
    public Response query(Map<String, String> parameters) {
        logger.info("#################### PARAM: {}", parameters);

        // TODO: get the form sql statement... execute it... return results
        jdbcTemplate.query("select 1", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return null;
            }
        });

        return Response.ok().build();
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
