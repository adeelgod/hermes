package com.m11n.hermes.core.service;

import com.m11n.hermes.core.model.BankStatement;

import java.util.List;
import java.util.Map;

public interface BankService {
    BankStatement convert(Map<String, String> entry) throws Exception;

    BankStatement save(BankStatement bs);

    boolean exists(BankStatement bs);

    List<BankStatement> listMatched();

    List<BankStatement> listUnmatched();

    void match();

    boolean matchRunning();

    void matchCancel();

    List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or);

    boolean processRunning();

    void process(List<BankStatement> bankStatements);

    void processCancel();

    void reload();
}
