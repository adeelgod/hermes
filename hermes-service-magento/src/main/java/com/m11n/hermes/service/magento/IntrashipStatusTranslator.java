package com.m11n.hermes.service.magento;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
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

    private String baseSplitPattern = "<br(\\s*)/>";

    private Pattern splitPattern = Pattern.compile("(.*)" + baseSplitPattern + "(.*)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private Pattern splitPatternWithoutPlaceHolder = Pattern.compile(baseSplitPattern, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

    @PostConstruct
    public void init() {
        successPattern = Pattern.compile(success, Pattern.CASE_INSENSITIVE);
        errorPattern = Pattern.compile(error, Pattern.CASE_INSENSITIVE);
        retryPattern = Pattern.compile(retry, Pattern.CASE_INSENSITIVE);
        warningPattern = Pattern.compile(warning, Pattern.CASE_INSENSITIVE);
    }

    public String toStatus(String message) {
        String status = "unknown";

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

        return status;
    }

    public boolean check(List<String> messages, String status) {
        return check(messages, Arrays.asList(status));
    }

    public boolean check(List<String> messages, List<String> stati) {
        for(String message : messages) {
            for(String status : stati) {
                if(toStatus(message).equals(status)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> normalizeMessage(String message) {
        logger.debug("normalizeMessage :: " + message);
        message = message.replaceAll("\n", "");
        List<String> result = new ArrayList<>();
        Matcher matcher = splitPattern.matcher(message);
        if(matcher.matches()) {
            logger.debug("matcher.matches()");
            for(String m : message.split(baseSplitPattern)) {
                m = m.trim();
                if(!StringUtils.isEmpty(m)) {
                    result.add(m);
                }
            }
        }  else {
            Matcher splitMatcher = splitPatternWithoutPlaceHolder.matcher(message);
            if(splitMatcher.matches()) {
                logger.debug("splitMatcher.matches()");
                for(String m : message.split(baseSplitPattern)) {
                    m = m.trim();
                    if(!StringUtils.isEmpty(m)) {
                        result.add(m);
                    }
                }
            } else {
                logger.debug("matcher.matches() else");
                message = message.replaceAll("\\<[^>]*>", "\n");
                for(String m : message.split("\n")) {
                    m = m.trim();
                    if(!StringUtils.isEmpty(m)) {
                        result.add(m);
                    }
                }
            }
        }
        return result;
    }

}
