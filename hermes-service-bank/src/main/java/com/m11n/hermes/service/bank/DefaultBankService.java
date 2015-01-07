package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.BankStatementPattern;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BankStatementPatternRepository;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.similarity.StringSimilarityService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private StringSimilarityService similarityService;

    @Inject
    private MagentoService magentoService;

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

            // NOTE: just take the first match
            if(m.find()) {
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", ""); // TODO: optional?

                // NOTE: only set if empty
                if(bs.property(bsp.getAttribute()).get()==null) {
                    bs.property(bsp.getAttribute()).set(value);
                }
                /**
                if(bsp.getStopOnFirstMatch()) {
                    break;
                }
                 */
            }
        }

        if(!StringUtils.isEmpty(bs.getOrderId())) {
            List<Map<String, Object>> result = auswertungRepository.findOrdersByOrderId(bs.getOrderId());

            if(result!=null && !result.isEmpty()) {
                Map<String, Object> order = result.get(0);
                bs.setFirstname(ObjectUtils.defaultIfNull(order.get("firstname"), "").toString());
                bs.setLastname(ObjectUtils.defaultIfNull(order.get("lastname"), "").toString());
                Double amount = (Double)ObjectUtils.defaultIfNull(order.get("amount"), 0.0d);
                bs.setAmountDiff(bs.getAmount().subtract(new BigDecimal(amount)));
                if(order.get("ebayName")!=null) {
                    bs.setEbayName(order.get("ebayName").toString());
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
        bs.setAmount(new BigDecimal(nf.parse(entry.get("amount")).doubleValue()));
        bs.setCurrency(entry.get("currency"));

        return bs;
    }

    public List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or) {
        return auswertungRepository.findOrderByFilter(uuid, lastnameCriteria, amount, amountDiff, lastname, orderId, or);
    }

    public List<Map<String, Object>> getOrders(String orderId) {
        return auswertungRepository.findOrdersByOrderId(orderId);
    }

    public BankStatement assign(BankStatement bs) {
        // TODO: invoke webservices
        bs.setStatus("confirmed");
        bankStatementRepository.updateStatus(bs.getId(), bs.getStatus());
        return bs;
    }

    public BankStatement ignore(BankStatement bs) {
        bs.setStatus("ignored");
        bankStatementRepository.updateStatus(bs.getId(), bs.getStatus());
        return bs;
    }

    public BankStatement reset(BankStatement bs) {
        // TODO: anything else to process here?
        bs.setStatus("new");
        bankStatementRepository.updateStatus(bs.getId(), bs.getStatus());
        return bs;
    }
}
