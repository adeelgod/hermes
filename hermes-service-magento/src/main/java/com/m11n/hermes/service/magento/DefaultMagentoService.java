package com.m11n.hermes.service.magento;

import com.m11n.hermes.core.dto.MagentoOrderServiceResponseDTO;
import com.m11n.hermes.core.model.MagentoOrderServiceAction;
import com.m11n.hermes.core.util.PropertiesUtil;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

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

    public Properties getApiConfig(String target, String action) throws Exception {
        Properties config = PropertiesUtil.getProperties();
        Properties apiConfig = new Properties();

        String configkey = "";
        switch(action) {
            case "shipment":
                configkey = "hermes.shipment." + target + ".api1.url";
                apiConfig.setProperty("url", config.getProperty(configkey));

                configkey = "hermes.shipment." + target + ".api.username";
                apiConfig.setProperty("username", config.getProperty(configkey));

                configkey = "hermes.shipment." + target + ".api.password";
                apiConfig.setProperty("password", config.getProperty(configkey));

                break;
            case "label":
                configkey = "hermes.shipment." + target + ".api2.url";
                apiConfig.setProperty("url", config.getProperty(configkey));

                configkey = "hermes.shipment." + target + ".api.username";
                apiConfig.setProperty("username", config.getProperty(configkey));

                configkey = "hermes.shipment." + target + ".api.password";
                apiConfig.setProperty("password", config.getProperty(configkey));
                break;
            default:
                break;
        }

        return apiConfig;
    }

    public String convertResponseToString(String strJson) {
        JSONObject jsonObject = new JSONObject(strJson);
        String strResult = (String)jsonObject.get("result");
        String strMessage = (String)jsonObject.get("message");

        String result = strResult + " :: " + strMessage;

        return result;
    }

    @Override
    public String createShipment(String target, String orderId) throws Exception {
        //orderId = "300277659";

		logger.debug("********* Create Shipment: {}", orderId);
		
        Properties apiConfig = getApiConfig(target, "shipment");
        String url = apiConfig.getProperty("url") + "&login=" + apiConfig.getProperty("username") + "&password=" + apiConfig.getProperty("password") + "&id=" + orderId;
		
		logger.debug("Shipment API URL :: " + url);

        Request req = new Request.Builder().url(url).build();
        Response res = client.newCall(req).execute();
        logger.debug("Response received ");
        final String resp = res.body().string();
        try {
            res.body().close();
        } catch(Exception e) {
            logger.debug("createShipment ERROR : ", e);
        }

        return resp;

    }

    @Override
    public String createShippingLabel(String target, String orderId) throws Exception {
        //orderId = "300277659";
        Properties apiConfig = getApiConfig(target, "label");

        String url = apiConfig.getProperty("url") + "&login=" + apiConfig.getProperty("username") + "&password=" + apiConfig.getProperty("password") + "&id=" + orderId;

        Request req = new Request.Builder().url(url).build();
        Response res = client.newCall(req).execute();
        logger.debug("Response received ");
        final String resp = res.body().string();
        try {
            res.body().close();
        } catch(Exception e) {
            logger.debug("createShippingLabel ERROR : ", e);
        }

        return resp;

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
    public MagentoOrderServiceResponseDTO callOrderService(String shop, String orderId, MagentoOrderServiceAction action) {
        logger.debug("################ CALL ORDER SERVICE: shop: {}, order id {}, action {}", shop, orderId, action.getValue());

        Map<String,String> params = new HashMap<>();
        params.put("name", shop);
        params.put("login", orderServiceUsername);
        params.put("password", orderServicePassword);
        params.put("id", orderId);
        params.put("action", action.getValue());

        return restTemplate.getForObject(
                orderServiceUrl,
                MagentoOrderServiceResponseDTO.class,
                params
        );
    }

    @Override
    protected List<String> doCreateIntrashipLabel(String target, String orderId) throws Exception {
        //orderId = "300277659";
        Properties apiConfig = getApiConfig(target, "label");
        logger.debug("********* DO CREATE INTRASHIP LABEL: {}", orderId);
        client.setConnectTimeout(timeout, TimeUnit.SECONDS); // connect timeout
        client.setReadTimeout(timeout, TimeUnit.SECONDS);    // socket timeout
        final String url = apiConfig.getProperty("url") + "&login=" + apiConfig.getProperty("username") + "&password=" + apiConfig.getProperty("password") + "&id=" + orderId;
        logger.debug("Shipping Label API URL :: " + url);
        Request req = new Request.Builder().url(url).build();
        Response res = client.newCall(req).execute();
        logger.debug("Response received ");
        String resp = res.body().string();
        resp = convertResponseToString(resp);
        try {
        	res.body().close();
        } catch(Exception e) {
        	logger.debug("doCreateIntrashipLabel ERROR : ", e);
        }
        return intrashipStatusTranslator.normalizeMessage(resp);
    }
}
