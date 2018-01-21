package com.m11n.hermes.rest.server.processor;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.PostalStamp;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.persistence.FairSheaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;

@Component
public class HermesStampProcessor {

    @Inject
    private FairSheaRepository fairSheaRepository;
    private static final Logger logger = LoggerFactory.getLogger(HermesStampProcessor.class);

    @Inject
    private BankService bankService;

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Transactional
    public void process(File file) {
        final String fileName = file.getName();
        logger.debug("Postal Stamp : " + fileName);
        try {
            if (fileName != null && fileName.length() > 4) {
                final String value = fileName.substring(0, 4);
                final PostalStamp postalStamp = new PostalStamp();
                postalStamp.setFileName(fileName);
                postalStamp.setValue(Double.parseDouble(value));
                fairSheaRepository.insertPostalStamp(postalStamp);
                logger.debug("successfully process postal stamp");
            } else {
                logger.warn("Postal stamp has invalid value : [" + fileName + "]");
            }
        } catch (final Exception ex) {
            logger.error("HermesStampProcessor : process(file) : ", ex);
        }
    }
}
