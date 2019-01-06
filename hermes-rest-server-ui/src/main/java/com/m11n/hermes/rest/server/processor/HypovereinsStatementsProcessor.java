package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.FinanceChannel;
import com.m11n.hermes.core.model.IntegrationReport;
import com.m11n.hermes.core.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class HypovereinsStatementsProcessor {
    @Inject
    private BankService bankService;

    @Transactional
    public void process(List<Map<String, String>> entries) {
        IntegrationReport report = bankService.importStatements(FinanceChannel.HYPOVEREINSBANK, entries);
        log.info("Hypovereinsbank import done. {}", report.getSummary());
    }
}
