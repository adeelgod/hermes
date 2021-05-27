package com.m11n.hermes.core.service;

import com.m11n.hermes.core.dto.MagentoOrderServiceResponseDTO;
import com.m11n.hermes.core.model.MagentoOrderServiceAction;

import java.util.List;
import java.util.Map;

public interface MagentoService {

    void ping() throws Exception;

    Map<String, Object> getShipmentInfo(String shipmentId) throws Exception;

    String createShipment(String target, String orderId) throws Exception;

    String createSalesOrderInvoice(String orderId) throws Exception;

    void completeInvoice(String orderId) throws Exception;

    List<Map<String, Object>> createIntrashipLabel(String target, String orderId) throws Exception;

    String createShippingLabel(String target, String orderId) throws Exception;

    List<String> getIntrashipStatuses(String orderId);

    MagentoOrderServiceResponseDTO callOrderService(String shop, String orderId, MagentoOrderServiceAction action);
}
