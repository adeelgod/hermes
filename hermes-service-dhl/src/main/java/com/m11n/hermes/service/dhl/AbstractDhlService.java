package com.m11n.hermes.service.dhl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.net.HttpHeaders;
import com.m11n.hermes.core.model.DhlRequest;
import com.m11n.hermes.core.model.DhlTrackingStatus;
import com.m11n.hermes.core.service.DhlService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractDhlService implements DhlService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDhlService.class);

    protected MediaType MEDIA_TYPE_XML;

    protected OkHttpClient client = new OkHttpClient();

    protected ObjectMapper mapper;

    @Inject
    protected AuswertungRepository auswertungRepository;

    protected String encoding = "UTF-8";

    protected Map<String, String> statusMapping = new HashMap<>();

    protected ExecutorService executor = Executors.newFixedThreadPool(1);

    protected AtomicInteger running = new AtomicInteger(0);

    public AbstractDhlService() {
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper = new XmlMapper();
        mapper.registerModule(module);

        loadStatusMappings();
    }

    private void loadStatusMappings() {
        try {
            List<String> lines = IOUtils.readLines(AbstractDhlService.class.getClassLoader().getResourceAsStream("status.csv"));

            for(String line : lines) {
                String[] pair = line.split("\\|");
                statusMapping.put(pair[0].toLowerCase(), pair[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String get(String url, DhlRequest r) {
    	Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url + "?xml=" + URLEncoder.encode(marshal(r), encoding))
                    .addHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_XML.toString())
                    .build();
            response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        	try {
        		response.body().close();
        	} catch (Exception e) {}
        }
    }

    protected String get(String url) {
    	Response response = null;
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
        	try {
				response.body().close();
        	} catch (Exception e) {}
        }
    }

    protected String marshal(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean trackingCheckStatus() {
        return (running.get() > 0);
    }

    public void checkTracking() {
        if(running.get()<=0) {
            scheduleCheckTracking();
        } else {
            logger.warn("Please cancel first all running tracking code checks.");
        }
    }

    public synchronized void cancelTracking() throws Exception {
        try {
            executor.shutdownNow();
            executor = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            // ignore
        }
        running.set(0);

        logger.warn("Track code checking cancelled.");
    }

    private void scheduleCheckTracking() {
        running.incrementAndGet();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> codes = auswertungRepository.findPendingTrackingCodes();

                    logger.debug("Checking codes: #{}", (codes!=null ? codes.size() : 0));

                    for(String code : codes) {
                        if(Thread.interrupted()) {
                            logger.warn("Cancelling tracking check at: {}", code);
                            running.decrementAndGet();
                            return;
                        }

                        DhlTrackingStatus status = getTrackingStatus(code);

                        auswertungRepository.createDhlStatus(code, status.getDate(), status.getMessage());
                        auswertungRepository.updateOrderLastStatus(code, getStatus(status.getMessage()));
                    }
                } catch (Throwable e) {
                    logger.error(e.toString(), e);
                } finally {
                    running.decrementAndGet();
                }
            }
        });
    }

    private String getStatus(String message) {
        for(Map.Entry<String, String> entry : statusMapping.entrySet()) {
            if(message!=null && message.toLowerCase().startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }

        logger.error("Status not found for message: {}", message);

        return "fehler";
    }
}
