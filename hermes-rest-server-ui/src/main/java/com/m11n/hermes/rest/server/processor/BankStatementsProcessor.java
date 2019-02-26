package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.IntegrationReport;
import com.m11n.hermes.core.service.BankService;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.List;

@Component
@Slf4j
public class BankStatementsProcessor {
    @Inject
    private BankService bankService;

    // Explicitly requested not to rollback when one of rows failed to be inserted
    @Transactional(value="financeTransactionManager", noRollbackFor = Exception.class)
    public void process(Exchange exchange, List<List<String>> entries) {
        Assert.notNull(exchange.getProperty("name"));
        Assert.notNull(exchange.getProperty("expectedColumns"));
        Assert.notNull(exchange.getProperty("statement"));

        String bankName = ((String) exchange.getProperty("name")).toUpperCase();
        String statement = (String) exchange.getProperty("statement");
        Integer expectedColumns = Integer.parseInt((String) exchange.getProperty("expectedColumns"));

        log.info("Starting bank import for {}", bankName);
        IntegrationReport report = bankService.importStatements(
                statement,
                expectedColumns,
                entries);

        log.info("{} import {}. {}",
                bankName,
                report.getFailCount()>0 ? "FAILED" : "COMPLETED",
                report.getSummary());

        if (report.getFailCount()>0) {
            throw new RuntimeException("Import failed");
        }
    }
}
