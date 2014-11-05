package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.FormFieldRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/forms/fields")
@Produces(APPLICATION_JSON)
public class FormFieldResource {
    private static final Logger logger = LoggerFactory.getLogger(FormFieldResource.class);

    @Inject
    private FormFieldRepository formFieldRepository;

    @GET
    @Path("{uid}")
    @Produces(APPLICATION_JSON)
    public Response get(@PathParam("uid") String uid) {
        return Response.ok(formFieldRepository.findOne(uid)).build();
    }

    @GET
    @Produces(APPLICATION_JSON)
    public Response list(@QueryParam("formId") String formId) {
        return Response.ok(formFieldRepository.findByFormId(formId)).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response save(FormField field) {
        return Response.ok(formFieldRepository.save(field)).build();
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    public Response remove(@QueryParam("uid") String uid) {
        formFieldRepository.delete(uid);
        return Response.ok().build();
    }
}
