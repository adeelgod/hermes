package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.util.PropertiesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;
import java.util.Properties;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/security")
@Produces(APPLICATION_JSON)
@Controller
public class SecurityResource {
    private static final Logger logger = LoggerFactory.getLogger(SecurityResource.class);

    @Value("${hermes.admin.password}")
    private String adminPassword;

    @POST
    @Path("/login")
    @Produces(APPLICATION_JSON)
    public Response login(Map<String, String> login) throws Exception {
        Properties p = PropertiesUtil.getProperties();

        if("admin".equals(login.get("username")) && adminPassword.equals(login.get("password"))) {
            return Response.ok().build();
        }

        return Response.serverError().build();
    }
}
