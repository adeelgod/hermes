package com.m11n.hermes.service.dhl;

import com.google.common.net.HttpHeaders;
import com.m11n.hermes.core.model.DhlRequest;
import com.m11n.hermes.core.model.DhlTrackingStatus;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Proxy;

@Service
public class DefaultDhlService extends AbstractDhlService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDhlService.class);

    private String cigUsername;

    private String cigPassword;

    private String appName;

    private String password;

    private String languageCode;

    private MODE mode;

    private String encoding = "UTF-8";

    private String baseUrl = "https://cig.dhl.de/services/";

    private String trackingUrl;

    public DefaultDhlService() {

    }

    public DefaultDhlService(String cigUsername, String cigPassword, String appName, String password, MODE mode) {
        this(cigUsername, cigPassword, appName, password, "en", "ISO-8859-1", mode);
    }

    public DefaultDhlService(final String cigUsername, final String cigPassword, String appName, String password, String languageCode, String encoding, MODE mode) {
        super();

        this.cigUsername = cigUsername;
        this.cigPassword = cigPassword;
        this.appName = appName;
        this.password = password;
        this.languageCode = languageCode;
        this.encoding = encoding;
        this.mode =  mode;
        this.MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=" + encoding);

        this.trackingUrl = baseUrl + mode.name().toLowerCase() + "/rest/sendungsverfolgung";

        client.setAuthenticator(new Authenticator() {
            @Override
            public Request authenticate(Proxy proxy, Response response) throws IOException {
                logger.debug("Authenticating for response: " + response);
                logger.debug("Challenges: " + response.challenges());

                String credential = Credentials.basic(cigUsername, cigPassword);

                return response.request().newBuilder()
                        .header(HttpHeaders.AUTHORIZATION, credential)
                        .build();
            }

            @Override
            public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                return null;
            }
        });
    }

    public DhlTrackingStatus getTrackingStatus(String code) {
        DhlRequest request = createRequest("d-get-piece");
        request.setPieceCode(code);

        String response = get(trackingUrl, request);

        DhlTrackingStatus status = new DhlTrackingStatus();
        status.setMessage(response);

        logger.debug(response);

        // TODO: needs more work

        return status;
    }

    private DhlRequest createRequest(String name) {
        DhlRequest request = new DhlRequest(name);
        request.setAppName(appName);
        request.setPassword(password);
        request.setLanguageCode(languageCode);

        return request;
    }
}
