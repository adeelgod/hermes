package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BankStatementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
public class BankStatementProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BankStatementProcessor.class);

    @Inject
    private BankService bankService;

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Transactional
    public void process(List<Map<String, String>> entries) {
        for(Map<String, String> entry : entries) {
            try {
                BankStatement bs = bankService.convert(entry);
                if(!bankService.exists(bs)) {
                    bs = bankService.save(bs); // NOTE: necessary to have a reference ID
                    // NOTE: ugly, but needed to flush the transaction...
                    logger.debug("+++++++++++++++++++++++ Bank statements: {}", bankStatementRepository.count());
                    bs = bankService.extract(bs);
                    bankService.save(bs);
                } else {
                    logger.warn("Bank statement already imported: {}", bs);
                }
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }
    }
}
