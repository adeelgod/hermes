package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.BankStatement;
import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.BankStatementRepository;
import com.m11n.hermes.persistence.FormRepository;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@DependsOn("dataSourceJpa")
public class DataInitializer {
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Inject
    private FormRepository formRepository;

    @Inject
    private BankStatementRepository bankStatementRepository;

    @PostConstruct
    public void init() throws Exception {
        // orders
        Form form = formRepository.findByName("orders");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("orders");
            form.setDescription("Orders");
            form.setSystemForm(true);
            form.setPosition(1);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("orders.sql")));
            form.setPrintable(true);
            form.setAccessPublic(true);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1, "", "From", true, false));
            fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2, "", "Until", true, false));
            fields.add(new FormField("unId", FormField.Type.TEXT.name(), 4, "", "ID", false, true));
            fields.add(new FormField("invoiceId", FormField.Type.TEXT.name(), 5, "", "Invoice", false, true));
            fields.add(new FormField("orderId", FormField.Type.TEXT.name(), 6, "", "Order", false, true));

            FormField shippingId = new FormField("shippingId", FormField.Type.TEXT.name(), 7, "", "Shipping", false, true);
            shippingId.setWidth(150);
            fields.add(shippingId);

            fields.add(new FormField("firstname", FormField.Type.TEXT.name(), 8, "", "Firstname", false, true));
            fields.add(new FormField("lastname", FormField.Type.TEXT.name(), 9, "", "Lastname", false, true));
            fields.add(new FormField("email", FormField.Type.TEXT.name(), 10, "", "Email", false, true));

            form.setFields(fields);

            formRepository.save(form);
        }


        // update
        form = formRepository.findByName("update");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("update");
            form.setDescription("Synchronisation");
            form.setSystemForm(true);
            form.setPosition(2);
            form.setExecuteOnStartup(true);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("update.sql")));
            form.setAccessPublic(true);
            formRepository.save(form);
        }


        // shipping
        form = formRepository.findByName("shipping");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("shipping");
            form.setDescription("Shipping");
            form.setSystemForm(true);
            form.setPosition(3);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("shipping.sql")));
            form.setPrintable(true);
            form.setAccessPublic(true);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            // parameters
            //fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1, "", "From", true, false));
            //fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2, "", "Until", true, false));

            FormField statusField = new FormField("status", FormField.Type.TEXT.name(), 1, "", "Status", true, false);
            statusField.setLookup(new HashSet<>(Arrays.asList("processing", "%")));
            fields.add(statusField);

            // columns
            FormField orderId = new FormField("orderId", FormField.Type.TEXT.name(), 2, "", "Order", false, true);
            orderId.setWidth(60);
            fields.add(orderId);

            fields.add(new FormField("weight", FormField.Type.TEXT.name(), 3, "", "Weight", false, true));
            fields.add(new FormField("company", FormField.Type.TEXT.name(), 4, "", "Company", false, true));
            fields.add(new FormField("firstname", FormField.Type.TEXT.name(), 5, "", "Firstname", false, true));
            fields.add(new FormField("lastname", FormField.Type.TEXT.name(), 6, "", "Lastname", false, true));
            fields.add(new FormField("phone", FormField.Type.TEXT.name(), 7, "", "Phone", false, true));
            fields.add(new FormField("street1", FormField.Type.TEXT.name(), 8, "", "Street", false, true));
            fields.add(new FormField("street2", FormField.Type.TEXT.name(), 9, "", "", false, true));
            fields.add(new FormField("zip", FormField.Type.TEXT.name(), 10, "", "ZIP", false, true));
            fields.add(new FormField("city", FormField.Type.TEXT.name(), 11, "", "City", false, true));
            fields.add(new FormField("country", FormField.Type.TEXT.name(), 12, "", "Country", false, true));
            fields.add(new FormField("dhlAccount", FormField.Type.TEXT.name(), 13, "", "DHL #", false, true));

            form.setFields(fields);

            formRepository.save(form);
        }


        // bank
        form = formRepository.findByName("bank");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("bank");
            form.setDescription("Bank Zahlungen (normal)");
            form.setSystemForm(true);
            form.setPosition(4);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("bank.sql")));
            form.setPrintable(false);
            form.setAccessPublic(false);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            // parameters
            fields.add(new FormField("status", FormField.Type.TEXT.name(), 1, "new", "Status", true, false));
            fields.add(new FormField("matching_start", FormField.Type.NUMBER.name(), 1, "0", "Matching range start", true, false));
            fields.add(new FormField("matching_end", FormField.Type.NUMBER.name(), 2, "1", "Matching range end", true, false));

            form.setFields(fields);

            formRepository.save(form);
        }


        // bank advanced
        form = formRepository.findByName("bank_advanced");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("bank_advanced");
            form.setDescription("Bank Zahlungen (advanced)");
            form.setSystemForm(true);
            form.setPosition(4);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("bank_advanced.sql")));
            form.setPrintable(false);
            form.setAccessPublic(false);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            // parameters
            fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1, "", "From", true, false));
            fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2, "", "Until", true, false));
            FormField status = new FormField("status", FormField.Type.TEXT.name(), 3, "new", "Status", true, false);
            status.setLookup(new HashSet<>(Arrays.asList("new", "confirmed", "ignored", "%")));
            fields.add(status);
            fields.add(new FormField("orderId", FormField.Type.TEXT.name(), 4, "%", "Order ID", true, false));
            fields.add(new FormField("firstname", FormField.Type.TEXT.name(), 5, "%", "Firstname", true, false));
            fields.add(new FormField("lastname", FormField.Type.TEXT.name(), 6, "%", "Lastname", true, false));

            form.setFields(fields);

            formRepository.save(form);
        }


        // bank match
        form = formRepository.findByName("bank_match");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("bank_match");
            form.setDescription("Bank Zahlungen (Bestellungen)");
            form.setSystemForm(true);
            form.setPosition(5);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("bank_statement_match.sql")));
            form.setPrintable(false);
            form.setAccessPublic(false);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            // parameters
            fields.add(new FormField("uuid", FormField.Type.TEXT.name(), 1, "%", "Bank Statement ID", true, false));
            fields.add(new FormField("lookup", FormField.Type.NUMBER.name(), 2, "6", "Lookup period (in months)", true, false));

            form.setFields(fields);

            formRepository.save(form);
        }


        // bank sync
        form = formRepository.findByName("bank_sync");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("bank_sync");
            form.setDescription("Bank Zahlungen (Sync)");
            form.setSystemForm(true);
            form.setPosition(5);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("bank_statement_sync.sql")));
            form.setPrintable(false);
            form.setAccessPublic(false);
            form = formRepository.save(form);

            formRepository.save(form);
        }


        // bank list matched
        form = formRepository.findByName("bank_list_matched");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("bank_list_matched");
            form.setDescription("Bank Zahlungen (Match List)");
            form.setSystemForm(true);
            form.setPosition(6);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("bank_statement_list_matched.sql")));
            form.setPrintable(false);
            form.setAccessPublic(false);
            form = formRepository.save(form);

            formRepository.save(form);
        }

        /**
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy"); // 26.03.2015
        long count = bankStatementRepository.count();
        for(BankStatement bs : bankStatementRepository.findAll()) {
            String tmp = bs.getAccount()
                    + df.format(bs.getTransferDate())
                    + bs.getReceiver1()
                    + bs.getReceiver2()
                    + bs.getDescription().replaceAll(" ", "").replaceAll("\\.", "")
                    + bs.getAmount()
                    + bs.getCurrency();

            bs.setHash(DigestUtils.sha384Hex(tmp));
            bankStatementRepository.save(bs);
            logger.info("# {}", count--);
        }
         */
    }
}
