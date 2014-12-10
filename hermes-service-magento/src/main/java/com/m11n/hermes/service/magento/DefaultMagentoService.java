package com.m11n.hermes.service.magento;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Profile("production")
@Service
public class DefaultMagentoService extends AbstractMagentoService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMagentoService.class);

    private OkHttpClient client = new OkHttpClient();

    private String sessionId;

    private Mage_Api_Model_Server_V2_HandlerPortType magentoService;

    @PostConstruct
    public void init() throws Exception {
        MagentoServiceLocator locator = new MagentoServiceLocator();
        magentoService = locator.getMage_Api_Model_Server_V2_HandlerPort();
        logger.debug("********* PRODUCTION - MAGENTO SERVICE INITIALIZED");
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
    protected List<String> doCreateIntrashipLabel(String orderId) throws Exception {
        logger.debug("********* DO CREATE INTRASHIP LABEL: {}", orderId);
        Request req = new Request.Builder().url(url + "/shipment/?login=" + username + "&password=" + password + "&id=" + orderId).build();
        Response res = client.newCall(req).execute();

        return intrashipStatusTranslator.normalizeMessage(res.body().string());
    }
}
