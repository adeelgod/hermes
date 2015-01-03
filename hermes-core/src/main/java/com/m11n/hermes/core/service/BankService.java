package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.BankStatement;

import java.util.List;
import java.util.Map;

public interface BankService {
    BankStatement convert(Map<String, String> entry) throws Exception;

    BankStatement save(BankStatement bs);

    BankStatement extract(BankStatement bs);

    List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or);

    List<Map<String, Object>> getOrders(String orderId);

    BankStatement assign(BankStatement bs);

    BankStatement ignore(BankStatement bs);

    BankStatement reset(BankStatement bs);

    void reload();
}
