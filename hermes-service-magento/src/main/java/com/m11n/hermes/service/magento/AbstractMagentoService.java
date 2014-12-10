package com.m11n.hermes.service.magento;

import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.SalesFlatShipmentCommentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import java.util.*;

public abstract class AbstractMagentoService implements MagentoService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractMagentoService.class);

    @Value("${hermes.magento.api.url}")
    protected String url;

    @Value("${hermes.magento.api.username}")
    protected String username;

    @Value("${hermes.magento.api.password}")
    protected String password;

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
