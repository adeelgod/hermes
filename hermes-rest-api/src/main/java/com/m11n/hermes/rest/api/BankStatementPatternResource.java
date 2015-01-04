package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.BankStatementPattern;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.persistence.BankStatementPatternRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/bank/patterns")
@Produces(APPLICATION_JSON)
@Controller
public class BankStatementPatternResource {
    private static final Logger logger = LoggerFactory.getLogger(BankStatementPatternResource.class);

    @Inject
    private BankService bankService;

    @Inject
    private QueryScheduler queryScheduler;

    @Inject
    private BankStatementPatternRepository bankStatementPatternRepository;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankStatementPatternRepository.findAll()).cacheControl(cc).build();
    }

    @POST
    @Produces(APPLICATION_JSON)
    public Response assign(BankStatementPattern bsp) {
        return Response.ok(bankStatementPatternRepository.save(bsp)).build();
    }

    @DELETE
    @Produces(APPLICATION_JSON)
    public Response remove(@QueryParam("id") String id) {
        bankStatementPatternRepository.delete(id);
        return Response.ok().build();
    }
}
