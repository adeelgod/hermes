package com.m11n.hermes.core.service;

import java.util.List;
import java.util.Map;

public interface MagentoService {

    void ping() throws Exception;

    Map<String, Object> getShipmentInfo(String shipmentId) throws Exception;

    String createShipment(String orderId) throws Exception;

    String createSalesOrderInvoice(String orderId) throws Exception;

    void completeInvoice(String orderId) throws Exception;

    List<Map<String, Object>> createIntrashipLabel(String orderId) throws Exception;

    List<String> getIntrashipStatuses(String orderId);
}
