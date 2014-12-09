package com.m11n.hermes.service.magento;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.regex.Pattern;

@Component
public class IntrashipStatusTranslator {
    private static final Logger logger = LoggerFactory.getLogger(IntrashipStatusTranslator.class);

    @Value("${hermes.magento.intraship.status.success}")
    private String success;

    @Value("${hermes.magento.intraship.status.error}")
    private String error;

    @Value("${hermes.magento.intraship.status.retry}")
    private String retry;

    @Value("${hermes.magento.intraship.status.warning}")
    private String warning;

    private Pattern successPattern;

    private Pattern errorPattern;

    private Pattern retryPattern;

    private Pattern warningPattern;

    @PostConstruct
    public void init() {
        successPattern = Pattern.compile(success, Pattern.CASE_INSENSITIVE);
        errorPattern = Pattern.compile(error, Pattern.CASE_INSENSITIVE);
        retryPattern = Pattern.compile(retry, Pattern.CASE_INSENSITIVE);
        warningPattern = Pattern.compile(warning, Pattern.CASE_INSENSITIVE);
    }

    public String toStatus(String message) {
        String status = "unknown";

        if("<br />".equals(message)) {
            status = "error";
        } else {
            try {
                if(successPattern.matcher(message).matches()) {
                    status = "success";
                } else if(errorPattern.matcher(message).matches()) {
                    status = "error";
                } else if(retryPattern.matcher(message).matches()) {
                    status = "retry";
                } else if(warningPattern.matcher(message).matches()) {
                    status = "warning";
                }
            } catch (Exception e) {
                logger.error(e.toString(), e);
            }
        }

        return status;
    }
}
