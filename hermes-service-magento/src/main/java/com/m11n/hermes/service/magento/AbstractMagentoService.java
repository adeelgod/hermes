package com.m11n.hermes.service.magento;

import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.SalesFlatShipmentCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractMagentoService implements MagentoService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMagentoService.class);

    @Value("${hermes.magento.api.url}")
    protected String url;
    
    @Value("${hermes.magento.api.username}")
    protected String username;

    @Value("${hermes.magento.api.password}")
    protected String password;

    @Value("${hermes.shipment.api.url}")
    protected String shipmentUrl;

    @Value("${hermes.shipment.api.username}")
    protected String shipmentUsername;
    
    @Value("${hermes.shipment.api.password}")
    protected String shipmentPassword;
    
    @Value("${hermes.invoice.api.url}")
    protected String invoiceUrl;
    
    @Value("${hermes.invoice.api.username}")
    protected String invoiceUsername;
    
    @Value("${hermes.invoice.api.password}")
    protected String invoicePassword;
    
    @Value("${hermes.magento.api.retry.max:3}")
    protected Integer retryMax;

    @Value("${hermes.magento.api.timeout:1000}")
    protected Integer timeout;
    
    @Value("${hermes.magento.api.retry.wait:500}")
    protected Long retryWait;

    @Inject
    protected SalesFlatShipmentCommentRepository salesFlatShipmentCommentRepository;

    @Inject
    protected AuswertungRepository auswertungRepository;

    @Inject
    protected IntrashipStatusTranslator intrashipStatusTranslator;

    @Value("${hermes.magento.intraship.status.retry.count}")
    protected int intrashipRetryCount;

    @Value("${hermes.magento.intraship.status.retry.wait}")
    protected long intrashipRetryWait;

    protected AtomicInteger retry = new AtomicInteger();

    protected String sessionId;

    protected Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    protected void init() throws Exception {
        try {
            MagentoServiceLocator locator = new MagentoServiceLocator();
            magentoService = locator.getMage_Api_Model_Server_V2_HandlerPort();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    protected boolean checkSession() {
        try {
            if(sessionId==null) {
                logger.info("New session. Logging in...");
                login();
            } else {
                ping();
            }
            retry.set(0);
            return true;
        } catch (Exception e) {
            logger.warn("Possible Magento session timeout - {}.", e.getMessage());
            retry();
        }

        return false;
    }

    protected boolean login() throws Exception {
        sessionId = magentoService.login(username, password);
        return (sessionId!=null);
    }

    protected void retry() {
        logger.warn("RETRY: Trying to login again.");

        while(retry.incrementAndGet()<=retryMax) {
            sessionId = null;
            logger.warn("RETRY: Waiting {}ms before trying again.", retryWait);

            try {
                Thread.sleep(retryWait);


                if(login()) {
                    logger.warn("RETRY: Re-try success!");
                    return;
                }
            } catch (Exception e) {
                // ignore
                logger.warn(e.getMessage());
            }
        }

        logger.warn("RETRY: Reached maximum re-tries of {}", retryMax);
        reset();
    }

    protected void reset() {
        sessionId = null;
        retry.set(0);
    }

    @Override
    public void ping() throws Exception {
        ApiEntity[] entities = magentoService.resources(sessionId);
        boolean ok = (entities!=null && entities.length>0);
        logger.debug("Session OK: {}", ok);

        if(!ok) {
            throw new RuntimeException("Ping failed.");
        }
        //MagentoInfoEntity info = magentoService.magentoInfo(sessionId);
        //logger.debug("Session OK: {} - {}", info.getMagento_version(), info.getMagento_edition());
    }

    @Override
    public List<Map<String, Object>> createIntrashipLabel(String orderId) throws Exception {
        List<Map<String, Object>> stati = new ArrayList<>();

        int attempts = 0;

        try {
            List<String> messages = doCreateIntrashipLabel(orderId);

            logger.debug("********* CREATE INTRASHIP: {} - {}", orderId, messages);

            if(!intrashipStatusTranslator.check(messages, Arrays.asList("retry"))) {
                attempts++;
                logger.debug("********* CREATE INTRASHIP EXIT: {}", orderId);
                addAll(stati, messages, orderId, attempts);
            } else {
                for(int i=0; i<intrashipRetryCount; i++) {
                    attempts++;

                    messages = doCreateIntrashipLabel(orderId);

                    logger.debug("********* CREATE INTRASHIP RETRY: {} - # {} of {}", orderId, (i+1), intrashipRetryCount);

                    addAll(stati, messages, orderId, i+2);

                    if(intrashipStatusTranslator.check(messages, Arrays.asList("retry"))) {
                        logger.debug("********* CREATE INTRASHIP WAIT: {}ms", intrashipRetryWait);
                        Thread.sleep(intrashipRetryWait);
                    } else {
                        return stati;
                    }
                }
            }
        } catch(Exception e) {
            logger.error("********* CREATE INTRASHIP FAILED: {} - {} - # {}", orderId, e.getMessage(), attempts);

            stati.add(createIntrashipResponse(orderId, "error", e.getMessage(), attempts));

            logger.error(e.toString(), e);
        }

        return stati;
    }

    private void addAll(List<Map<String, Object>> stati, List<String> messages, String orderId, int attempts) {
        for(String m : messages) {
            String status = intrashipStatusTranslator.toStatus(m);
            stati.add(createIntrashipResponse(orderId, status, m, attempts));
        }
    }

    abstract protected List<String> doCreateIntrashipLabel(String orderId) throws Exception;

    protected Map<String, Object> createIntrashipResponse(String orderId, String status, String message, int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("orderId", orderId);
        response.put("status", status);
        response.put("message", message);
        response.put("count", count);

        return response;
    }

    public List<String> getIntrashipStatuses(String orderId) {
        String shippingId = auswertungRepository.findShippingIdByOrderId(orderId);
        List<String> rawStatuses = salesFlatShipmentCommentRepository.findRawStatus(shippingId);

        List<String> result = new ArrayList<>();

        for(String rawStatus : rawStatuses) {
            result.add(intrashipStatusTranslator.toStatus(rawStatus));
        }

        return result;
    }
}
