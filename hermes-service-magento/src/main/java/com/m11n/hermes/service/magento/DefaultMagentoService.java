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
import java.util.concurrent.TimeUnit;

@Profile("production")
@Service
public class DefaultMagentoService extends AbstractMagentoService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultMagentoService.class);

    private OkHttpClient client = new OkHttpClient();

    @PostConstruct
    public void init() throws Exception {
        super.init();
        logger.debug("PRODUCTION - MAGENTO SERVICE INITIALIZED");
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
    public String createSalesOrderInvoice(String orderId) throws Exception {
        checkSession();
        String invoiceId = magentoService.salesOrderInvoiceCreate(sessionId, orderId, new OrderItemIdQty[0], "", "", "");

        return invoiceId;
    }

    @Override
    public void completeInvoice(String orderId) throws Exception {
        logger.debug("********* COMPLETE INVOICE: {}", orderId);
        Request req = new Request.Builder().url(invoiceUrl + "?login=" + invoiceUsername + "&password=" + invoicePassword + "&id=" + orderId).build();
        Response res = client.newCall(req).execute();
        logger.debug("********* COMPLETE INVOICE: {}", res.body().string());
        try {
        	res.body().close();
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

    @Override
    protected List<String> doCreateIntrashipLabel(String orderId) throws Exception {
        logger.debug("********* DO CREATE INTRASHIP LABEL: {}", orderId);
        client.setConnectTimeout(timeout, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(timeout, TimeUnit.SECONDS);    // socket timeout
        final String url = shipmentUrl + "?login=" + shipmentUsername + "&password=" + shipmentPassword + "&id=" + orderId;
        logger.debug("Shipment URL :: " + url);
        Request req = new Request.Builder().url(url).build();
        Response res = client.newCall(req).execute();
        logger.debug("Response received ");
        final String resp = res.body().string();
        try {
        	res.body().close();
        } catch(Exception e) {
        	logger.debug("doCreateIntrashipLabel ERROR : ", e);
        }
        return intrashipStatusTranslator.normalizeMessage(resp);
    }
}
