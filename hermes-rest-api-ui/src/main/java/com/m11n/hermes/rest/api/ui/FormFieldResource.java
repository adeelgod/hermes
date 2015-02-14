package com.m11n.hermes.rest.api.ui;

import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.FormFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/forms/fields")
@Produces(MediaType.APPLICATION_JSON)
public class FormFieldResource {
    private static final Logger logger = LoggerFactory.getLogger(FormFieldResource.class);

    @Inject
    private FormFieldRepository formFieldRepository;

    @GET
    @Path("{uid}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@PathParam("id") String id) {
        return Response.ok(formFieldRepository.findOne(id)).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response list(@QueryParam("formId") String formId) {
        return Response.ok(formFieldRepository.findByFormId(formId)).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(FormField field) {
        return Response.ok(formFieldRepository.save(field)).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response remove(@QueryParam("id") String id) {
        formFieldRepository.delete(id);
        return Response.ok().build();
    }
}
