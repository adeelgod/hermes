package com.m11n.hermes.core.service;

import com.m11n.hermes.core.dto.BankStatementDTO;
import com.m11n.hermes.core.model.BankMatchIcon;
import com.m11n.hermes.core.model.BankStatement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BankService {
    BankStatement convert(Map<String, String> entry) throws Exception;

    BankStatement save(BankStatement bs);

    boolean exists(BankStatement bs);

    List<BankStatementDTO> listMatched();

    Optional<BankMatchIcon> matchIconAndActions(List<BankMatchIcon> bankMatchIcons, String shop, String type, String status);

    List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or);

    boolean processRunning();

    void process(List<BankStatementDTO> bankStatements);

    void processCancel();
}
