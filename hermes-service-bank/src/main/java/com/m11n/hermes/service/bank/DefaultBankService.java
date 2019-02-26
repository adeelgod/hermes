package com.m11n.hermes.service.bank;

import com.m11n.hermes.core.dto.BankStatementDTO;
import com.m11n.hermes.core.dto.BankStatementProcessStatusDTO;
import com.m11n.hermes.core.exception.BankStatementDBUpdateException;
import com.m11n.hermes.core.exception.BankStatementMagentoUpdateException;
import com.m11n.hermes.core.exception.BankStatementTerminateException;
import com.m11n.hermes.core.model.*;
import com.m11n.hermes.core.service.BankService;
import com.m11n.hermes.core.service.MagentoService;
import com.m11n.hermes.core.util.ExceptionUtil;
import com.m11n.hermes.persistence.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@DependsOn("dataInitializer")
@Slf4j
public class DefaultBankService implements BankService {

    @Inject
    private BankStatementRepository bankStatementRepository;

    @Inject
    private BankRepository bankRepository;

    @Inject
    private FinanceDao finance;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private MagentoService magentoService;

    @Inject
    private FormRepository formRepository;

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

    private AtomicBoolean processRunning = new AtomicBoolean(false);
    private AtomicBoolean processTerminate = new AtomicBoolean(false);
    private AtomicInteger dbUpdatesDone = new AtomicInteger(0);
    private AtomicInteger magentoUpdatesDone = new AtomicInteger(0);
    private AtomicInteger totalUpdates = new AtomicInteger(0);

    @Value("${hermes.bank.statement.invoice.create:true}")
    private boolean createInvoiceEnabled = true;

    @Value("${hermes.bank.statement.invoice.complete:true}")
    private boolean completeInvoiceEnabled = true;

    @Transactional
    public BankStatement save(BankStatement bs) {
        return bankStatementRepository.save(bs);
    }

    @Transactional
    public boolean exists(BankStatement bs) {
        long count = bankStatementRepository.exists(bs.getAccount(), bs.getTransferDate(), bs.getDescriptionb(), bs.getAmount(), bs.getCurrency());
        log.info("Bank statement exists: {} - {}", count, bs);
        return (count>0L);
    }

    public IntegrationReport importStatements(String statement, Integer expectedColumns, List<List<String>> entries) {
        IntegrationReport report =  new IntegrationReport();
        for(List<String> entry : entries) {
            report.incrementProcessed();
            try {
                finance.importBankData(statement, expectedColumns, entry);
                report.incrementSuccess();
            } catch (Exception e) {
                report.reportFailureOnCurrentProcessed(ExceptionUtil.unwindException(e).getMessage());
            }
        }
        return report;
    }

    @Transactional
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
    @Transactional
    public List<BankStatementDTO> listMatched() {
        sync();

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

    private boolean eligibleForAction(String actualAction, String expectedAction) {
        return (actualAction != null && actualAction.equalsIgnoreCase(expectedAction));
    }

    private MagentoOrderServiceAction getMagentoAction(String bankMatchAction2) {
        if (eligibleForAction(bankMatchAction2, "shop_generate_invoice")) {
            return MagentoOrderServiceAction.GENERATE_INVOICE;
        } else if(eligibleForAction(bankMatchAction2, "shop_complete_after_inkasso")) {
            return MagentoOrderServiceAction.SET_STATUS_TO_COMPLETED;
        } else if(eligibleForAction(bankMatchAction2, "shop_complete")) {
            return MagentoOrderServiceAction.SET_STATUS_TO_COMPLETED;
        }
        return null;
    }

    @Transactional(value="financeTransactionManager", rollbackFor = Exception.class)
    public void process(final List<BankStatementDTO> bankStatements) throws BankStatementDBUpdateException, BankStatementMagentoUpdateException {
        if (!processRunning.get()) {
            processRunning.set(true);
            processTerminate.set(false);
            dbUpdatesDone.set(0);
            magentoUpdatesDone.set(0);
            totalUpdates.set(bankStatements.size());

            // Process action 1 (bank update) for each bank statement
            try {
                for (BankStatementDTO statement : bankStatements) {

                    if (eligibleForAction(statement.getAction1(), "change_bank")) {
                        log.debug("Update {} bank for: {} - {} - {}",
                                statement.getBank(),
                                statement.getShop(),
                                statement.getOrderId(),
                                statement.getId());

                        finance.updateBank(
                                FinanceChannel.getBank(statement.getBank()),
                                statement.getShop(),
                                statement.getOrderId(),
                                statement.getId());
                        dbUpdatesDone.incrementAndGet();
                    }

                    if (processTerminate.get()) {
                        throw new BankStatementTerminateException("Bank service terminating as DB operations");
                    }
                }
            } catch (Exception ex) {
                log.error("Error while processing action1 for statements: {}", ex.toString());
                throw new BankStatementDBUpdateException(ex);
            } finally {
                processTerminate.set(false);
                processRunning.set(false);
            }

            // Process action 2 (magento service call) for each bank statement
            try {
                for (BankStatementDTO statement : bankStatements) {
                    if (statement.getAction2() != null) {
                        log.debug("Invoke '{}' magento service for : action {}, shop {}, order id {}",
                                getMagentoAction(statement.getAction2()),
                                statement.getShop(),
                                statement.getAction2(),
                                statement.getOrderId());

                        magentoService.callOrderService(
                                statement.getShop(),
                                statement.getOrderId(),
                                getMagentoAction(statement.getAction2()));

                        magentoUpdatesDone.incrementAndGet();
                    }

                    if (processTerminate.get()) {
                        throw new BankStatementTerminateException("Bank service terminating at magento operations");
                    }
                }
            } catch (Exception ex) {
                log.error("Error while processing action2 (magento update) for statements: {}", ex.toString());
                throw new BankStatementMagentoUpdateException(ex);
            } finally {
                processTerminate.set(false);
                processRunning.set(false);
            }
        } else {
            log.warn("Please cancel all bank statement jobs first.");
        }
    }

    public synchronized BankStatementProcessStatusDTO processStatus() {
        return new BankStatementProcessStatusDTO(
                magentoUpdatesDone.get(),
                dbUpdatesDone.get(),
                totalUpdates.get(),
                processRunning.get()
        );
    }

    public synchronized void processCancel() {
        try {
            processTerminate.set(true);
        } catch (Exception e) {
            log.error("Bank statement processing cancellation failed.", e);
        }
        processRunning.set(false);

        log.warn("Bank statement processing cancelled.");
    }

    @Transactional
    public List<Map<String, Object>> filter(String uuid, String lastnameCriteria, boolean amount, boolean amountDiff, boolean lastname, String orderId, boolean or) {
        return auswertungRepository.findBankStatementOrderByFilter(uuid, lastnameCriteria, amount, amountDiff, lastname, orderId, or);
    }

    @Transactional
    public void sync() {
        // Leaving this for a record only, according to chat with Daniel
        // "general sync is not needed here, it should be run only on global sync and application startup"
        // Form updateForm = formRepository.findByName("update");
        // auswertungRepository.update(updateForm.getSqlStatement(), Collections.emptyMap());

        Form bankSyncForm = formRepository.findByName("bank_sync_new");
        auswertungRepository.update(bankSyncForm.getSqlStatement(), Collections.emptyMap());
    }
}
