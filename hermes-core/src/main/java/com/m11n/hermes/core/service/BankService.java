package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.BankStatement;

import java.util.Map;

public interface BankService {
    BankStatement convert(Map<String, String> entry) throws Exception;

    BankStatement save(BankStatement bs);

    BankStatement extract(BankStatement bs);
}
