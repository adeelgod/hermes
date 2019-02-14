package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.FinanceChannel;
import com.m11n.hermes.core.model.IntegrationReport;
import com.m11n.hermes.core.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;

@Component
@Slf4j
public class PaypalStatementsProcessor {

    @Inject
    private BankService bankService;

    @Transactional(value="financeTransactionManager", rollbackFor = Exception.class)
    public void process(List<List<String>> entries) {
        log.info("Starting Paypal import for {} entries", entries.size());
        IntegrationReport report = bankService.importStatements(FinanceChannel.PAYPAL, entries);
        log.info("Paypal import {}. {}",
                report.getFailCount()>0 ? "FAILED" : "COMPLETED",
                report.getSummary());
        if (report.getFailCount()>0) {
            throw new RuntimeException("Import failed");
        }
    }
}
