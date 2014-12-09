package com.m11n.hermes.service.magento;

import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.SalesFlatShipmentCommentRepository;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DefaultMagentoService implements MagentoService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMagentoService.class);

    private OkHttpClient client = new OkHttpClient();

    @Value("${hermes.magento.api.url}")
    private String url;

    @Value("${hermes.magento.api.username}")
    private String username;

    @Value("${hermes.magento.api.password}")
    private String password;

    private String sessionId;

    private Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    @Inject
    private SalesFlatShipmentCommentRepository salesFlatShipmentCommentRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private IntrashipStatusTranslator intrashipStatusTranslator;

    @Value("${hermes.magento.intraship.status.retry.count}")
    private int intrashipRetryCount;

    @Value("${hermes.magento.intraship.status.retry.wait}")
    private long intrashipRetryWait;

    @PostConstruct
    public void init() throws Exception {
        MagentoServiceLocator locator = new MagentoServiceLocator();
        magentoService = locator.getMage_Api_Model_Server_V2_HandlerPort();
    }

    private void checkSession() throws Exception {
        try {
            if(sessionId==null) {
                logger.info("New Magento session. Logging in...");
                sessionId = magentoService.login(username, password);
            } else {
                logger.debug("Checking session...");
                // TODO: fix this!
                //MagentoInfoEntity info = magentoService.magentoInfo(sessionId);
                //logger.debug("Session OK: {} - {}", info.getMagento_version(), info.getMagento_edition());
            }
        } catch (Exception e) {
            try {
                logger.warn("Possible Magento session timeout. Trying to login again.");
                sessionId = magentoService.login(username, password);
            } catch (Exception ex) {
                logger.error("Login failed.");
                logger.error(e.toString(), e);
                sessionId = null;
            }
        }
    }

    @Override
    public Map<String, Object> getShipmentInfo(String shippingId) throws Exception {
        checkSession();
        SalesOrderShipmentEntity shipment = magentoService.salesOrderShipmentInfo(sessionId, shippingId);

        Map<String, Object> result = new HashMap<>();
        result.put("active", shipment.getIs_active());
        result.put("created", shipment.getCreated_at());
        result.put("orderId", shipment.getOrder_increment_id());
        result.put("firstname", shipment.getShipping_firstname());
        result.put("lastname", shipment.getShipping_lastname());
        result.put("quantity", shipment.getTotal_qty());

        return result;
    }

    @Override
    public String createShipment(String orderId) throws Exception {
        checkSession();
        String shipmentId = magentoService.salesOrderShipmentCreate(sessionId, orderId, new OrderItemIdQty[0], "", 0, 0);

        logger.info("********* CREATE SHIPMENT: {} - {}", orderId, shipmentId);

        return shipmentId;
    }

    @Override
    public Map<String, Object> createIntrashipLabel(String orderId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        result.put("orderId", orderId);

        try {
            Request req = new Request.Builder().url(url + "/shipment/?login=" + username + "&password=" + password + "&id=" + orderId).build();
            Response res = client.newCall(req).execute();

            String message = res.body().string();
            String status = intrashipStatusTranslator.toStatus(message);

            logger.info("********* CREATE INTRASHIP: {} - {} ({})", orderId, message, status);

            // TODO: retry logic

            if("success".equals(status) || "warning".equals(status)) {
                result.put("status", status);
                result.put("message", message);
                result.put("count", 1);

                return result;
            } else if("retry".equals(status)) {
                for(int i=0; i<intrashipRetryCount; i++) {
                    res = client.newCall(req).execute();

                    message = res.body().string();
                    status = intrashipStatusTranslator.toStatus(message);

                    if("success".equals(status) || "warning".equals(status)) {
                        result.put("status", status);
                        result.put("message", message);
                        result.put("count", i+2);

                        return result;
                    }
                    logger.warn("Retry Intraship label: {} - #{} of {}", orderId, i, intrashipRetryCount);
                    Thread.sleep(intrashipRetryWait);
                }
            }
        } catch(Exception e) {
            result.put("status", "error");
            result.put("message", e.getMessage());
            logger.error(e.toString(), e);
        }

        return result;
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
