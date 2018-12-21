package com.m11n.hermes.service.magento;

import com.m11n.hermes.core.dto.MagentoOrderServiceResponseDTO;
import com.m11n.hermes.core.model.MagentoOrderServiceAction;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Profile("debug")
@Service
public class DebugMagentoService extends AbstractMagentoService {

    private static final Logger logger = LoggerFactory.getLogger(DebugMagentoService.class);

    private AtomicInteger messageCounter = new AtomicInteger();

    private List<String> messages = new ArrayList<>();

    private AtomicInteger ping = new AtomicInteger();

    @PostConstruct
    public void init() {
        try {
            super.init();
            logger.debug("DEBUG - MAGENTO SERVICE INITIALIZED");

            LineIterator iterator = IOUtils.lineIterator(DebugMagentoService.class.getClassLoader().getResourceAsStream("debug_label_status_messages.txt"), "UTF-8");

            while(iterator.hasNext()) {
                messages.add(iterator.next());
            }
        } catch(Exception e) {
            logger.error(e.toString(), e);
        }
    }

    /**
    protected boolean login() throws Exception {
        throw new RuntimeException("Break!");
    }

    protected boolean login() throws Exception {
        logger.debug("LOGIN: {}/{}", username, password);
        return super.login();
    }
     */

    @Override
    public void ping() throws Exception {
        super.ping();

        if(ping.incrementAndGet()%4==0) {
            throw new RuntimeException("Fake ping exception");
        }
    }

    @Override
    public Map<String, Object> getShipmentInfo(String shipmentId) throws Exception {
        logger.debug("################ DEBUG - SHIPMENT INFO: {}", shipmentId);
        checkSession();
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
        logger.debug("################ DEBUG - CREATE SHIPMENT: {}", orderId);
        checkSession();
        Thread.sleep(500L);
        return new Date().getTime()+"";
    }

    @Override
    public String createSalesOrderInvoice(String orderId) throws Exception {
        logger.debug("################ DEBUG - CREATE SALES ORDER INVOICE: {}", orderId);
        checkSession();
        Thread.sleep(500L);
        return new Date().getTime()+"";
    }

    @Override
    public void completeInvoice(String orderId) throws Exception {
        logger.debug("################ DEBUG - COMPLETE INVOICE: {}", orderId);
        checkSession();
        Thread.sleep(500L);
    }

    @Override
    public MagentoOrderServiceResponseDTO callOrderService(String orderId, MagentoOrderServiceAction action) {
        logger.debug("################ DEBUG - CALL ORDER SERVICE: order id {}, action {}", orderId, action.getValue());
        return new MagentoOrderServiceResponseDTO("success", "Success");
    }

    @Override
    protected List<String> doCreateIntrashipLabel(String orderId) throws Exception {
        logger.debug("################ DEBUG - DO CREATE INTRASHIP LABEL: {}", orderId);
        checkSession();
        Thread.sleep(2000L);
        String message = messages.get(messageCounter.getAndIncrement() % messages.size());
        logger.debug("################ DEBUG - INTRASHIP MESSAGE: {}", message);
        return intrashipStatusTranslator.normalizeMessage(message);
    }
}
