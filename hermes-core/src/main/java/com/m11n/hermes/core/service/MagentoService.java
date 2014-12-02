package com.m11n.hermes.core.service;

import java.util.Map;

public interface MagentoService {

    Map<String, Object> getShipmentInfo(String shipmentId) throws Exception;

    String createShipment(String orderId) throws Exception;

    String createIntrashipLabel(String orderId) throws Exception;
}
