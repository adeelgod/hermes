package com.m11n.hermes.rest.api;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.persistence.util.QueryScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/bank/statements")
@Produces(APPLICATION_JSON)
@Controller
public class BankStatementResource {
    private static final Logger logger = LoggerFactory.getLogger(BankStatementResource.class);

    @Inject
    private BankService bankService;

    @Inject
    private QueryScheduler queryScheduler;

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @GET
    @Produces(APPLICATION_JSON)
    public Response list() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankStatementRepository.findByStatusAndAmountGreaterThan("new", new BigDecimal(0.0))).cacheControl(cc).build();
    }

    @GET
    @Path("filter")
    @Produces(APPLICATION_JSON)
    public Response filter(@QueryParam("uuid") String uuid, @QueryParam("lastnameCriteria") @DefaultValue("") String lastnameCriteria, @QueryParam("amount") @DefaultValue("false") boolean amount, @QueryParam("amountDiff") @DefaultValue("false") boolean amountDiff, @QueryParam("lastname") @DefaultValue("false") boolean lastname, @QueryParam("or") @DefaultValue("false") boolean or) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankService.filter(uuid, lastnameCriteria, amount, amountDiff, lastname, or)).cacheControl(cc).build();
    }

    @POST
    @Path("assign")
    @Produces(APPLICATION_JSON)
    public Response assign(BankStatement bs) {
        return Response.ok(bankService.assign(bs)).build();
    }

    @POST
    @Path("ignore")
    @Produces(APPLICATION_JSON)
    public Response ignore(BankStatement bs) {
        return Response.ok(bankService.ignore(bs)).build();
    }

    @POST
    @Path("reset")
    @Produces(APPLICATION_JSON)
    public Response reset(BankStatement bs) {
        return Response.ok(bankService.reset(bs)).build();
    }
}
