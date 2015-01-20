package com.m11n.hermes.service.dhl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.google.common.net.HttpHeaders;
import com.m11n.hermes.core.model.DhlRequest;
import com.m11n.hermes.core.service.DhlService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;

public abstract class AbstractDhlService implements DhlService {
    private static final Logger logger = LoggerFactory.getLogger(AbstractDhlService.class);

    protected MediaType MEDIA_TYPE_XML;

    protected OkHttpClient client = new OkHttpClient();

    protected ObjectMapper mapper;

    private String encoding = "UTF-8";

    public AbstractDhlService() {
        JaxbAnnotationModule module = new JaxbAnnotationModule();
        mapper = new XmlMapper();
        mapper.registerModule(module);
    }

    protected String get(String url, DhlRequest r) {
        try {
            Request request = new Request.Builder()
                    .url(url + "?xml=" + URLEncoder.encode(marshal(r), encoding))
                    .addHeader(HttpHeaders.ACCEPT, MEDIA_TYPE_XML.toString())
                    .build();
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String get(String url) {
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();

            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String marshal(Object o) {
        try {
            return mapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
