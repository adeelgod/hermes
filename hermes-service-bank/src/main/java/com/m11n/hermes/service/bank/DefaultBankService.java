package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.dto.BankStatementDTO;
import com.m11n.hermes.core.model.BankMatchIcon;
import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BankRepository;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@DependsOn("dataInitializer")
@Transactional
public class DefaultBankService implements BankService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultBankService.class);

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Inject
    private BankRepository bankRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private MagentoService magentoService;

    @Inject
    private FormRepository formRepository;

    @Inject
    @Named("jdbcTemplateAuswertung")
    protected NamedParameterJdbcTemplate jdbcTemplate;

    private final static String[] REPLACEMENTS = new String[] {
            " ", "\\.", ",", ";", "-", "\\?", ":",
            "SEPABASISLASTSCHRIFTIMAUFTRV",
            "SEPAGUTSCHRIFT",
            "SEPABASISLASTSCHRIFT",
            "ZAHLUNGSGRUND",
            "AUFTRAGGEBER",
            "MANDATSREF",
            "IMAUFTRV",
            "SEPAONLINEÜBERWEISUNG",
            "EUSTANDARDÜBERWEISUNGELEEUStandardüberweisungelektronischerteilt",
            "SEPACASHMANAGEMENTGUTSCH",
            "SEPAONLINEBERWEISUNGEMPFNGER",
            "EMPFNGER",
            "AUFTRAGGEBER",
            "SEPAONLINEBERWEISUNGEMPFNGER",
            "SEPACASHMANAGEMENTGUTSCHIMAUFTRV"
    };

    @Value("${hermes.bank.statement.lookup.period:6}")
    private int lookupPeriod = 6;

    @Value("${hermes.bank.statement.auto.assignment.threshold:90}")
    private int autoAssignmentThreshold = 90;

    private ExecutorService processExecutor = Executors.newFixedThreadPool(1);

    private AtomicInteger processRunning = new AtomicInteger(0);

    @Value("${hermes.bank.statement.invoice.create:true}")
    private boolean createInvoiceEnabled = true;

    @Value("${hermes.bank.statement.invoice.complete:true}")
    private boolean completeInvoiceEnabled = true;

    public BankStatement save(BankStatement bs) {
        return bankStatementRepository.save(bs);
    }

    public boolean exists(BankStatement bs) {
        long count = bankStatementRepository.exists(bs.getAccount(), bs.getTransferDate(), bs.getDescriptionb(), bs.getAmount(), bs.getCurrency());
        logger.info("Bank statement exists: {} - {}", count, bs);
        return (count>0L);
    }

    public BankStatement convert(Map<String, String> entry) throws Exception {
        DecimalFormat nf = (DecimalFormat)DecimalFormat.getInstance(Locale.GERMAN);
        nf.setMaximumFractionDigits(3);
        nf.setParseBigDecimal(true);
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        String descriptionb = entry.get("description").toUpperCase();

        for(String replacement : REPLACEMENTS) {
            descriptionb = descriptionb.replaceAll(replacement, "");
        }

        descriptionb = descriptionb.replaceAll("Ä", "AE").replaceAll("Ö", "OE").replaceAll("Ü", "UE").replaceAll("ß", "SS");

        if(descriptionb.length()>255) {
            descriptionb = descriptionb.substring(0, 255);
        }

        // NOTE: leave out valuta date, b/c always the same as transfer date; minimizing data items
        String tmp = entry.get("account")
                + entry.get("transferDate")
                + entry.get("receiver1")
                + entry.get("receiver2")
                + descriptionb
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
        bs.setDescriptionb(descriptionb);
        bs.setAmount((BigDecimal)nf.parse(entry.get("amount")));
        bs.setCurrency(entry.get("currency"));

        return bs;
    }

    @Override
    public List<BankStatementDTO> listMatched() {
        //sync();

        List<BankMatchIcon> bankMatchIcons = bankRepository.getAllBankMatchIconAndActions();

        return bankRepository
                .getAllMatched()
                .stream()
                .peek((s)-> {
                    Optional<BankMatchIcon> bm = matchIconAndActions(bankMatchIcons, s.getShop(), s.getType(), s.getOrderStatus());
                    if (bm.isPresent()) {
                        s.setOrderIcon(bm.get().getIcon());
                        s.setAction1(bm.get().getAction1());
                        s.setAction2(bm.get().getAction2());
                    }
                })
                .collect(Collectors.toList());
    }

    public Optional<BankMatchIcon> matchIconAndActions(List<BankMatchIcon> bankMatchIcons, String shop, String type, String status) {
        return bankMatchIcons
                .stream()
                .filter(m -> shop.equalsIgnoreCase(m.getShop()) && status.equalsIgnoreCase(m.getStatus()))
                .filter(m -> m.getType().equals("%") || type.equalsIgnoreCase(m.getType()))
                .findFirst();
    }

    public boolean processRunning() {
        return (processRunning.get() > 0);
    }

    public void process(final List<BankStatementDTO> bankStatements) {
        if(processRunning.get()<=0) {
            processRunning.incrementAndGet();

            processExecutor.execute(() -> {

                // Process action 1 for each bank statement
                try {
                    for (BankStatementDTO bankStatement : bankStatements) {
                        BankStatement orig = bankStatementRepository.findOne(bankStatement.getId());

                        orig.setInvoiceId(bankStatement.getInvoiceId());
                        orig.setCustomerId(bankStatement.getCustomerId());
                        orig.setStatus(bankStatement.getStatus());
                        orig.setOrderId(bankStatement.getOrderId());
                        orig.setEbayName(bankStatement.getEbayName());
                        orig.setFirstname(bankStatement.getFirstName());
                        orig.setLastname(bankStatement.getLastName());

                        bankStatementRepository.save(orig);

                        auswertungRepository.updateOrderPaymentId(bankStatement.getOrderId(), bankStatement.getId());

                        try {
                            if("confirm".equals(bankStatement.getStatus())) {
                                List<Map<String, Object>> orders = auswertungRepository.findOrdersByOrderId(bankStatement.getOrderId());

                                if(orders!=null && !orders.isEmpty()) {
                                    Map<String, Object> order = orders.get(0);

                                    String status = order.get("status").toString();
                                    String type = order.get("type").toString();

                                    if("ebay_vorkasse".equalsIgnoreCase(type) || "shop_vorkasse".equalsIgnoreCase(type)) {
                                        if(createInvoiceEnabled) {
                                            logger.debug("Invoke webservice for (*** ENABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                            magentoService.createSalesOrderInvoice(bankStatement.getOrderId());
                                            // TODO: maybe set flag that webservice was executed
                                        } else {
                                            logger.debug("Invoke webservice for (*** DISABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                        }
                                    } else if("shop_rechnung".equalsIgnoreCase(type)) {
                                        if(completeInvoiceEnabled) {
                                            logger.debug("Invoke webservice for (*** ENABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                            magentoService.completeInvoice(bankStatement.getOrderId());
                                            // TODO: maybe set flag that webservice was executed
                                        } else {
                                            logger.debug("Invoke webservice for (*** DISABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                        }
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            logger.warn("Magento service call failed: {}", t.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.toString(), e);
                } finally {
                    processRunning.set(0);
                }
            });
        } else {
            logger.warn("Please cancel all bank statement jobs first.");
        }
    }

    public void processOld(final List<BankStatementDTO> bankStatements) {
        if(processRunning.get()<=0) {
            processRunning.incrementAndGet();

            processExecutor.execute(() -> {
                try {
                    for (BankStatementDTO bankStatement : bankStatements) {
                        BankStatement orig = bankStatementRepository.findOne(bankStatement.getId());

                        orig.setInvoiceId(bankStatement.getInvoiceId());
                        orig.setCustomerId(bankStatement.getCustomerId());
                        orig.setStatus(bankStatement.getStatus());
                        orig.setOrderId(bankStatement.getOrderId());
                        orig.setEbayName(bankStatement.getEbayName());
                        orig.setFirstname(bankStatement.getFirstName());
                        orig.setLastname(bankStatement.getLastName());

                        bankStatementRepository.save(orig);

                        auswertungRepository.updateOrderPaymentId(bankStatement.getOrderId(), bankStatement.getId());

                        try {
                            if("confirm".equals(bankStatement.getStatus())) {
                                List<Map<String, Object>> orders = auswertungRepository.findOrdersByOrderId(bankStatement.getOrderId());

                                if(orders!=null && !orders.isEmpty()) {
                                    Map<String, Object> order = orders.get(0);

                                    String status = order.get("status").toString();
                                    String type = order.get("type").toString();

                                    if("ebay_vorkasse".equalsIgnoreCase(type) || "shop_vorkasse".equalsIgnoreCase(type)) {
                                        if(createInvoiceEnabled) {
                                            logger.debug("Invoke webservice for (*** ENABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                            magentoService.createSalesOrderInvoice(bankStatement.getOrderId());
                                            // TODO: maybe set flag that webservice was executed
                                        } else {
                                            logger.debug("Invoke webservice for (*** DISABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                        }
                                    } else if("shop_rechnung".equalsIgnoreCase(type)) {
                                        if(completeInvoiceEnabled) {
                                            logger.debug("Invoke webservice for (*** ENABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                            magentoService.completeInvoice(bankStatement.getOrderId());
                                            // TODO: maybe set flag that webservice was executed
                                        } else {
                                            logger.debug("Invoke webservice for (*** DISABLED ***): {} - {} - {}", order.get("orderId"), type, status);
                                        }
                                    }
                                }
                            }
                        } catch (Throwable t) {
                            logger.warn("Magento service call failed: {}", t.getMessage());
                        }
                    }
                } catch (Throwable e) {
                    logger.error(e.toString(), e);
                } finally {
                    processRunning.set(0);
                }
            });
        } else {
            logger.warn("Please cancel first all bank statement jobs.");
        }
    }

    public synchronized void processCancel() {
        try {
            processExecutor.shutdownNow();
            processExecutor = Executors.newSingleThreadExecutor();
        } catch (Exception e) {
            // ignore
        }
        processRunning.set(0);

        logger.warn("Bank statement processing cancelled.");
    }

    public List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or) {
        return auswertungRepository.findBankStatementOrderByFilter(uuid, lastnameCriteria, amount, amountDiff, lastname, orderId, or);
    }

    private void sync() {
        // Leaving this for a record only, according to chat with Daniel "general sync is not needed anymore"
        // Form updateForm = formRepository.findByName("update");
        // auswertungRepository.update(updateForm.getSqlStatement(), Collections.emptyMap());

        Form bankSyncForm = formRepository.findByName("bank_sync_new");
        auswertungRepository.update(bankSyncForm.getSqlStatement(), Collections.emptyMap());
    }
}
