package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.BankStatementPattern;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.persistence.BankStatementPatternRepository;
import com.m11n.hermes.persistence.BankStatementRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@DependsOn("dataInitializer")
public class DefaultBankService implements BankService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBankService.class);

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Inject
    private BankStatementPatternRepository bankStatementPatternRepository;

    private Set<BankStatementPattern> patterns = new HashSet<>();

    @PostConstruct
    public void init() {
        reload();
    }

    public void reload() {
        for(BankStatementPattern bsp : bankStatementPatternRepository.findAll()) {
            if(bsp.getCaseInsensitive()) {
                bsp.setRegex(Pattern.compile(bsp.getPattern(), Pattern.CASE_INSENSITIVE));
            } else {
                bsp.setRegex(Pattern.compile(bsp.getPattern()));
            }

            logger.debug("++++++++++++++++++++++++++++ PATTERN: {}", bsp.getPattern());
            patterns.add(bsp);
        }
    }

    public BankStatement save(BankStatement bs) {
        BankStatement result = bankStatementRepository.findByHash(bs.getHash());

        if(result==null) {
            bs = bankStatementRepository.save(bs);
            return bs;
        }

        logger.warn("Bank statement exists already: {} (equals={})", result, result.equals(bs));

        return result;
    }

    public BankStatement extract(BankStatement bs) {
        for(BankStatementPattern bsp : patterns) {
            Matcher m = bsp.getRegex().matcher(bs.getDescription());

            if(m.matches()) {
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", ""); // TODO: optional?
                bs.property(bsp.getAttribute()).set(value);
                logger.debug("++++++++++++++++++++++++++++ PROPERTY: {} = {}", bsp.getAttribute(), value);
                if(bsp.getStopOnFirstMatch()) {
                    break;
                }
            }
        }

        return bs;
    }

    public BankStatement convert(Map<String, String> entry) throws Exception {
        NumberFormat nf = NumberFormat.getInstance(Locale.GERMAN);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        // NOTE: leave out valuta date, b/c always the same as transfer date; minimizing data items
        String tmp = entry.get("account")
                + entry.get("transferDate")
                + entry.get("receiver1")
                + entry.get("receiver2")
                + entry.get("description")
                + entry.get("amount")
                + entry.get("currency");

        BankStatement bs = new BankStatement();
        bs.setHash(DigestUtils.sha384Hex(tmp));
        bs.setAccount(entry.get("account"));
        bs.setTransferDate(df.parse(entry.get("transferDate")));
        bs.setValutaDate(df.parse(entry.get("valutaDate")));
        bs.setReceiver1(entry.get("receiver1"));
        bs.setReceiver2(entry.get("receiver2"));
        bs.setDescription(entry.get("description"));
        bs.setAmount(nf.parse(entry.get("amount")).doubleValue());
        bs.setCurrency(entry.get("currency"));

        return bs;
    }
}
