package com.m11n.hermes.service.dhl;

import com.google.common.net.HttpHeaders;
import com.m11n.hermes.core.model.DhlRequest;
import com.m11n.hermes.core.model.DhlTrackingStatus;
import com.m11n.hermes.core.util.DhlApiLanguage;
import com.squareup.okhttp.*;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.util.Date;

@Service
public class DefaultDhlService extends AbstractDhlService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultDhlService.class);

    // language code should be configurable
    @Value("${hermes.dhl.api.language}")
    private String languageCode;

    private MODE mode;

    private String encoding = "UTF-8";

//    OLD BASEURL
//    private String baseUrl = "https://cis.dhl.de/services/";

    private String baseUrl = "https://cig.dhl.de/services/";

    private String trackingUrl;

    private ISWSServicePortType ws;

    private Version wsVersion;

    @Value("${hermes.dhl.api.username}")
    private String wsUsername;

    @Value("${hermes.dhl.api.password}")
    private String wsPassword;

    @Value("${hermes.dhl.api.production}")
    private Boolean wsProduction;

    @Value("${hermes.dhl.cis.username}")
    private String cisUsername;

    @Value("${hermes.dhl.cis.password}")
    private String cisPassword;

    @Value("${hermes.dhl.cis.ekp}")
    private String cisEkp;

    public DefaultDhlService() throws Exception {
        this(null, null, null, null, DhlApiLanguage.DEUTSCHLAND.getValue(), "UTF-8", MODE.PRODUCTION);
    }

    public DefaultDhlService(String cisUsername, String cisPassword, String wsUsername, String wsPassword, MODE mode) {
        this(cisUsername, cisPassword, wsUsername, wsPassword, DhlApiLanguage.DEUTSCHLAND.getValue(), "UTF-8", mode);
    }

    public DefaultDhlService(final String cisUsername, final String cisPassword, String wsUsername, String wsPassword, String languageCode, String encoding, MODE mode) {
        super();

        try {
            wsVersion = new Version();
            wsVersion.setMajorRelease("1");
            wsVersion.setMinorRelease("0");
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }

        this.cisUsername = cisUsername;
        this.cisPassword = cisPassword;
        this.wsPassword = wsPassword;
        this.languageCode = languageCode;
        this.encoding = encoding;
        this.mode = mode;
        this.MEDIA_TYPE_XML = MediaType.parse("application/xml; charset=" + encoding);

        this.trackingUrl = baseUrl + mode.name().toLowerCase() + "/rest/sendungsverfolgung";


    }

    @PostConstruct
    public void init() throws Exception {
        logger.debug("INSIDE INIT :: " + this.toString());
        this.mode = wsProduction ? MODE.PRODUCTION : MODE.SANDBOX;
        try {
            // soap
            ISService_1_0_deLocator locator = new ISService_1_0_deLocator();
            ws = locator.getShipmentServiceSOAP11port0(new URL("https://cig.dhl.de/services/" + mode.name().toLowerCase() + "/soap"));
            Stub stub = (Stub) ws;
            stub.setUsername(wsUsername);
            stub.setPassword(wsPassword);

            // soap authentication header
            SOAPHeaderElement authentication = new SOAPHeaderElement("http://dhl.de/webservice/cisbase", "Authentification");
            SOAPHeaderElement user = new SOAPHeaderElement("http://dhl.de/webservice/cisbase", "user", cisUsername);
            SOAPHeaderElement password = new SOAPHeaderElement("http://dhl.de/webservice/cisbase", "signature", cisPassword);
            SOAPHeaderElement type = new SOAPHeaderElement("http://dhl.de/webservice/cisbase", "type", "0");
            authentication.addChild(user);
            authentication.addChild(password);
            authentication.addChild(type);
            stub.setHeader(authentication);

            // rest
            client.setAuthenticator(new Authenticator() {
                @Override
                public Request authenticate(Proxy proxy, Response response) throws IOException {
                    logger.debug("Authenticating for response: " + response);
                    logger.debug("Challenges: " + response.challenges());

                    String credential = Credentials.basic(cisUsername, cisPassword);

                    return response.request().newBuilder()
                            .header(HttpHeaders.AUTHORIZATION, credential)
                            .build();
                }

                @Override
                public Request authenticateProxy(Proxy proxy, Response response) throws IOException {
                    return null;
                }
            });
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    public String getVersion() throws Exception {
        GetVersionResponse res = ws.getVersion(wsVersion);
        Version v = res.getVersion();

        return v.getMajorRelease() + "." + v.getMinorRelease() + "." + v.getBuild();
    }

    public DhlTrackingStatus getTrackingStatus(String code) {
        try {
            logger.info("\n\n\nINSIDE \n CLASS == DefaultDhlService \n METHOD == getTrackingStatus(); ");
            DhlRequest request = createRequest("d-get-piece");
            request.setPieceCode(code);

            String response = get(trackingUrl, request);

            DhlTrackingStatus status = new DhlTrackingStatus();
            status.setMessage(response);
            logger.info("Shipment status : " + status);

            logger.info("\n\nEXITING THIS METHOD \n\n\n");
            return status;
        } catch (Exception ex) {
            logger.error("ERROR OCCURRED :", ex);
            return null;
        }
    }


    @Scheduled(cron = "${hermes.dhl.tracking.cron}")
    public void checkTracking() {
        super.checkTracking();
    }

    /**
     * @Scheduled(cron = "${hermes.dhl.test.cron}")
     * public void testScheduler() {
     * logger.debug("----- Scheduler: {}", new Date());
     * }
     */

    private DhlRequest createRequest(String name) {
        DhlRequest request = new DhlRequest(name);
        request.setAppName(wsUsername);
        request.setPassword(wsPassword);
        request.setLanguageCode(languageCode);

        return request;
    }

    @Override
    public String toString() {
        final String dhlApiInformation = "languageCode : " + this.languageCode +
                " wsUsername : " + this.wsUsername +
                " wsPassword : " + this.wsPassword +
                " wsProduction : " + this.wsProduction;
        return dhlApiInformation;
    }
}
