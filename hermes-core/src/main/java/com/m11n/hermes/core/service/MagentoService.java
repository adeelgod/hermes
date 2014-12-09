package com.m11n.hermes.core.service;

import java.util.List;
import java.util.Map;

public interface MagentoService {

    Map<String, Object> getShipmentInfo(String shipmentId) throws Exception;

    String createShipment(String orderId) throws Exception;

    Map<String, Object> createIntrashipLabel(String orderId) throws Exception;

    List<String> getIntrashipStatuses(String orderId);
}
