package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.service.BankService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Component
public class BankStatementProcessor {
    private static final Logger logger = LoggerFactory.getLogger(BankStatementProcessor.class);

    @Inject
    private BankService bankService;

    public void process(List<Map<String, String>> entries) {
        for(Map<String, String> entry : entries) {
            try {
                BankStatement bs = bankService.convert(entry);
                bs = bankService.extract(bs);
                bankService.save(bs);
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }
    }
}
