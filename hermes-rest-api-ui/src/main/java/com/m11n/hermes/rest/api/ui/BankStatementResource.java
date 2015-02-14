package com.m11n.hermes.rest.api.ui;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

@Path("/bank/statements")
@Produces(MediaType.APPLICATION_JSON)
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
    @Path("matched")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listMatched() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankService.listMatched()).cacheControl(cc).build();
    }

    @GET
    @Path("unmatched")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listUnmatched() {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankService.listUnmatched()).cacheControl(cc).build();
    }

    @GET
    @Path("filter")
    @Produces(MediaType.APPLICATION_JSON)
    public Response filter(@QueryParam("uuid") String uuid, @QueryParam("lastnameCriteria") @DefaultValue("") String lastnameCriteria, @QueryParam("amount") @DefaultValue("false") boolean amount, @QueryParam("amountDiff") @DefaultValue("false") boolean amountDiff, @QueryParam("lastname") @DefaultValue("false") boolean lastname, @QueryParam("orderId") @DefaultValue("") String orderId, @QueryParam("or") @DefaultValue("false") boolean or) {
        CacheControl cc = new CacheControl();
        cc.setNoCache(true);

        return Response.ok(bankService.filter(uuid, lastnameCriteria, amount, amountDiff, lastname, orderId, or)).cacheControl(cc).build();
    }

    @GET
    @Path("match")
    @Produces(MediaType.APPLICATION_JSON)
    public Response match() {
        bankService.match();
        return Response.ok().build();
    }

    @GET
    @Path("match/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchCancel() {
        bankService.matchCancel();
        return Response.ok().build();
    }

    @GET
    @Path("match/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response matchRunning() {
        return Response.ok(bankService.matchRunning()).build();
    }

    @POST
    @Path("process")
    @Produces(MediaType.APPLICATION_JSON)
    public Response process(Map<String, Object> data) {
        String status = data.get("status").toString();
        List<String> statementIds = (List)data.get("ids");
        bankService.processStatus(statementIds, status);
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(BankStatement bs) {
        bankService.save(bs);
        return Response.ok().build();
    }

    @GET
    @Path("process/cancel")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processCancel() {
        bankService.processStatusCancel();
        return Response.ok().build();
    }

    @GET
    @Path("process/status")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processRunning() {
        return Response.ok(bankService.processStatusRunning()).build();
    }
}
