package com.m11n.hermes.service.magento;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DebugMagentoService extends AbstractMagentoService {

    private static final Logger logger = LoggerFactory.getLogger(DebugMagentoService.class);

    private AtomicInteger messageCounter = new AtomicInteger();

    private List<String> messages = new ArrayList<>();

    @PostConstruct
    public void init() {
        logger.debug("################ DEBUG - MAGENTO SERVICE INITIALIZED");

        try {
            LineIterator iterator = IOUtils.lineIterator(DebugMagentoService.class.getClassLoader().getResourceAsStream("debug_label_status_messages.txt"), "UTF-8");

            while(iterator.hasNext()) {
                messages.add(iterator.next());
            }
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }
    }

    @Override
    public Map<String, Object> getShipmentInfo(String shipmentId) throws Exception {
        logger.debug("################ DEBUG - SHIPMENT INFO: {}", shipmentId);
        Thread.sleep(500L);

        Map<String, Object> result = new HashMap<>();
        result.put("active", true);
        result.put("created", "2014-12-10 10:00:00");
        result.put("orderId", 30000001);
        result.put("firstname", "Daniel");
        result.put("lastname", "Schaaf");
        result.put("quantity", 1);

        return result;
    }

    @Override
    public String createShipment(String orderId) throws Exception {
        logger.debug("################ DEBUG - CREATE SHIPMENT: {} - {}", orderId);
        Thread.sleep(500L);
        return new Date().getTime()+"";
    }

    @Override
    protected List<String> doCreateIntrashipLabel(String orderId) throws Exception {
        logger.debug("################ DEBUG - DO CREATE INTRASHIP LABEL: {}", orderId);
        Thread.sleep(2000L);
        String message = messages.get(messageCounter.getAndIncrement() % messages.size());
        logger.debug("################ DEBUG - INTRASHIP MESSAGE: {}", message);
        return intrashipStatusTranslator.normalizeMessage(message);
    }
}
