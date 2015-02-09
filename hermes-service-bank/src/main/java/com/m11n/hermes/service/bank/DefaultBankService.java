package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.BankStatementPattern;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BankStatementPatternRepository;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@DependsOn("dataInitializer")
@Transactional
public class DefaultBankService implements BankService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBankService.class);

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Inject
    private BankStatementPatternRepository bankStatementPatternRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private MagentoService magentoService;

    @Inject
    private FormRepository formRepository;

    private Set<BankStatementPattern> patterns = new HashSet<>();

    private final static BigDecimal MATCH_THRESHOLD_0 = new BigDecimal(0.0d);

    private final static BigDecimal MATCH_THRESHOLD_80 = new BigDecimal(0.8d);

    private final static BigDecimal MATCH_THRESHOLD_100 = new BigDecimal(1.0d);

    private final static String[] ORDER_ATTRIBUTES = new String[] {
            "orderId", "amount", "ebayName", "firstname", "lastname", "matching"
    };

    @Value("${hermes.bank.statement.lookup.period:6}")
    private int lookupPeriod = 6;

    @Value("${hermes.bank.statement.auto.assignment.threshold:90}")
    private int autoAssignmentThreshold = 90;

    private Form bankStatementMatchForm;

    protected ExecutorService matchExecutor = Executors.newFixedThreadPool(1);

    protected AtomicInteger matchRunning = new AtomicInteger(0);

    protected ExecutorService processExecutor = Executors.newFixedThreadPool(1);

    protected AtomicInteger processRunning = new AtomicInteger(0);

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
        bankStatementMatchForm = formRepository.findByName("bank_match");
    }

    public BankStatement save(BankStatement bs) {
        return bankStatementRepository.save(bs);
    }

    public boolean exists(BankStatement bs) {
        return (bankStatementRepository.findByHash(bs.getHash())!=null);
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
        bs.setDescriptionb(bs.getDescription().replaceAll(" ", ""));
        bs.setAmount(new BigDecimal(nf.parse(entry.get("amount")).doubleValue()));
        bs.setCurrency(entry.get("currency"));

        return bs;
    }

    @Override
    public void match() {
        if(matchRunning.get()<=0) {
            matchRunning.incrementAndGet();

            matchExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sync();

                        // TODO: implement this
                        logger.debug("Trigger match statement here...");
                        Thread.sleep(10000);
                    } catch (Throwable e) {
                        logger.error(e.toString(), e);
                    } finally {
                        matchRunning.set(0);
                    }
                }
            });
        } else {
            logger.warn("Please cancel first all match jobs.");
        }
    }

    @Override
    public boolean matchRunning() {
        return (matchRunning.get() > 0);
    }

    @Override
    public void matchCancel() {
        try {
            matchExecutor.shutdownNow();
            matchExecutor= Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            // ignore
        }
        matchRunning.set(0);

        logger.warn("Match cancelled.");
    }

    public boolean processStatusRunning() {
        return (processRunning.get() > 0);
    }

    public void processStatus(final List<String> statementIds, final String status) {
        if(processRunning.get()<=0) {
            processRunning.incrementAndGet();

            processExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        sync();

                        for (String id : statementIds) {
                            setStatus(id, status);
                        }
                    } catch (Throwable e) {
                        logger.error(e.toString(), e);
                    } finally {
                        processRunning.set(0);
                    }
                }
            });
        } else {
            logger.warn("Please cancel first all bank statement jobs.");
        }
    }

    public synchronized void processStatusCancel() {
        try {
            processExecutor.shutdownNow();
            processExecutor = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            // ignore
        }
        processRunning.set(0);

        logger.warn("Bank statement processing cancelled.");
    }

    private void setStatus(String id, String status) {
        if("reset".equals(status)) {
            bankStatementRepository.updateStatusAndOrderId(id, "new", null);
        } else {
            bankStatementRepository.updateStatus(id, status);
        }

        if("confirm".equals(status)) {
            // TODO: check for type/status in order to trigger webservices

            // TODO: assign orders
            //auswertungRepository.assignBankstatementOrders(bs.getId(), bs.getOrderIds());
        }
    }
    public List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or) {
        return auswertungRepository.findBankStatementOrderByFilter(uuid, lastnameCriteria, amount, amountDiff, lastname, orderId, or);
    }

    private void sync() {
        Form form = formRepository.findByName("bank_sync");

        auswertungRepository.update(form.getSqlStatement(), Collections.<String, Object>emptyMap());
    }

    @Deprecated
    public BankStatement extract(BankStatement bs) {
        bs = extractFromMatch(bs);

        /**
        if(MATCH_THRESHOLD_80.compareTo(bs.getMatching())==1) {
            bs = extractFromDescription(bs);
        }
         */

        return bs;
    }

    @Deprecated
    private BankStatement extractFromMatch(BankStatement bs) {
        List<Map<String, Object>> orders = match(bs.getId());

        if(orders!=null && !orders.isEmpty()) {
            Map<String, Object> order = orders.get(0); // NOTE: best match

            for(String key : ORDER_ATTRIBUTES) {
                Object value = order.get(key);
                if(value instanceof Double) {
                    value = new BigDecimal((Double)value).setScale(2, BigDecimal.ROUND_HALF_EVEN);
                }

                bs.property(key).set(value);
            }
        }

        return bs;
    }

    @Deprecated
    private List<Map<String, Object>> match(String uuid) {
        Map<String, Object> params = new HashMap<>();
        params.put("uuid", uuid);
        params.put("lookup", lookupPeriod);

        return auswertungRepository.query(bankStatementMatchForm.getSqlStatement(), params, new AuswertungRepository.DefaultMapper());
    }

    @Deprecated
    private BankStatement extractFromDescription(BankStatement bs) {
        for(BankStatementPattern bsp : patterns) {
            Matcher m = bsp.getRegex().matcher(bs.getDescription());

            // NOTE: just take the first match
            if(m.find()) {
                if(MATCH_THRESHOLD_0.compareTo(bs.getMatching())==0) {
                    // NOTE: if we find any match here in general there is a confidence of 80% that it's OK
                    bs.setMatching(new BigDecimal(MATCH_THRESHOLD_80.doubleValue()).setScale(2, BigDecimal.ROUND_HALF_EVEN)); // NOTE: copy, only set it once
                }
                String value = m.group(bsp.getPatternGroup()).replaceAll(" ", ""); // TODO: optional?

                // NOTE: only set if empty
                if(bs.property(bsp.getAttribute()).get()==null) {
                    // TODO: actually we would need type checks here if stuff is not string
                    bs.property(bsp.getAttribute()).set(value);
                }
            }
        }

        if(!StringUtils.isEmpty(bs.getOrderId())) {
            List<Map<String, Object>> result = auswertungRepository.findOrdersByOrderId(bs.getOrderId());

            if(result!=null && !result.isEmpty()) {
                Map<String, Object> order = result.get(0);
                bs.setFirstname(ObjectUtils.defaultIfNull(order.get("firstname"), "").toString());
                bs.setLastname(ObjectUtils.defaultIfNull(order.get("lastname"), "").toString());
                Double amount = (Double)ObjectUtils.defaultIfNull(order.get("amount"), 0.0d);
                bs.setAmountDiff(bs.getAmount().subtract(new BigDecimal(amount)).setScale(2, BigDecimal.ROUND_HALF_EVEN));
                if(order.get("ebayName")!=null) {
                    bs.setEbayName(order.get("ebayName").toString());
                }
            }
        }

        return bs;
    }

    @Deprecated
    public List<Map<String, Object>> getOrders(String orderId) {
        return auswertungRepository.findOrdersByOrderId(orderId);
    }
}
