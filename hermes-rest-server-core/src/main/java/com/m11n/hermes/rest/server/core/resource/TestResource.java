package com.m11n.hermes.rest.server.core.resource;

import org.apache.commons.lang.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.container.TimeoutHandler;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.io.StringWriter;
import java.security.Principal;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/test")
@Produces(APPLICATION_JSON)
public class TestResource {

    private static final Logger logger = LoggerFactory.getLogger(TestResource.class);

    private Executor executor = new ThreadPoolExecutor(5, 5, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(10));

    @Context
    protected HttpServletRequest httpRequest;

    @Context
    protected SecurityContext securityContext;

    @PostConstruct
    public void init() {
        logger.info("================= Test JAX-RS resource initialized.");
    }

    // NOTE: fuck... this should be documented... everything in the pipeline has to be marked async-supported... otherwise nothing works
    //       see more here http://stackoverflow.com/questions/4104907/servlet-3-0-async-supported-does-not-work
    // NOTE: AsyncResponse injection only works as method parameter
    @GET
    @Path("/async/{timeout}")
    public void testAsync(@Suspended final AsyncResponse asyncResponse, @DefaultValue("5") @PathParam("timeout") Integer timeout) throws Exception {
        final StopWatch watch = new StopWatch();
        watch.reset();
        watch.start();

        asyncResponse.register(new CompletionCallback() {
            @Override
            public void onComplete(Throwable throwable) {
                if (throwable == null) {
                    // no throwable - the processing ended successfully
                    // (response already written to the client)
                    logger.info("================= ASYNC: complete.");
                } else {
                    logger.error(throwable.toString(), throwable);
                }
            }
        });
        asyncResponse.setTimeoutHandler(new TimeoutHandler() {
            @Override
            public void handleTimeout(AsyncResponse ar) {
                ar.resume(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity("Operation time out.").build());
            }
        });
        asyncResponse.setTimeout(timeout, TimeUnit.SECONDS);

        // NOTE: a separate thread doesn't
        executor.execute(new Runnable() {
            @Override
            public void run() {
                final Principal user = securityContext.getUserPrincipal();

                // NOTE: we don't need a separate executor service; Undertow takes care of this behind the scenes when XnioWorker is set
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                watch.stop();

                logger.info("================= ASYNC: user     ... {}", (user == null ? "-none-" : user.getName()));
                logger.info("================= ASYNC: end      ... {}", watch.toString());
                logger.info("================= ASYNC: cancelled... {}", asyncResponse.isCancelled());
                logger.info("================= ASYNC: done     ... {}", asyncResponse.isDone());
                logger.info("================= ASYNC: suspended... {}", asyncResponse.isSuspended());

                StringWriter writer = new StringWriter();

                writer.append("ASYNC: user     ... ").append(user == null ? "-none-" : user.getName()).append("<br>");
                writer.append("ASYNC: end      ... ").append(watch.toString()).append("<br>");
                writer.append("ASYNC: cancelled... ").append(asyncResponse.isCancelled()+"").append("<br>");
                writer.append("ASYNC: done     ... ").append(asyncResponse.isDone()+"").append("<br>");
                writer.append("ASYNC: suspended... ").append(asyncResponse.isSuspended()+"").append("<br>");

                asyncResponse.resume(Response.ok(writer.toString()).build());
            }
        });
    }

    @GET
    @Path("/scheme")
    public Response scheme() {
        String scheme = securityContext.getAuthenticationScheme();

        logger.info("================= Scheme: {}", scheme);

        return Response.ok(scheme).build();
    }

    /**
    @GET
    @Path("/user/shiro")
    public Response user() {
        Object user = SecurityUtils.getSubject().getPrincipal();

        logger.info("================= User: {}", user);

        return Response.ok(user+"").build();
    }

    @GET
    @Path("/role/shiro/{name}")
    public Response role(@PathParam("name") String name) {
        boolean result = SecurityUtils.getSubject().hasRole(name);

        logger.info("================= ROLE: {} = {}", name, result);

        return Response.ok(result).build();
    }
     */

    @GET
    @Path("/user/jaxrs")
    public Response userJaxRs() {
        Principal user = securityContext.getUserPrincipal();

        logger.info("================= User: {}", user);

        return Response.ok(user+"").build();
    }

    @GET
    @Path("/role/jaxrs/{name}")
    public Response roleJaxRs(@PathParam("name") String name) {
        boolean result = securityContext.isUserInRole(name);

        logger.info("================= ROLE: {} = {}", name, result);

        return Response.ok(result).build();
    }
}
