package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.model.LabelStatus;
import com.m11n.hermes.persistence.FormRepository;
import com.m11n.hermes.persistence.LabelStatusRepository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Component
@DependsOn("sshTunnel")
public class DataInitializer {

    @Inject
    private FormRepository formRepository;

    @Inject
    private LabelStatusRepository labelStatusRepository;

    private String[] labelStatusFiles = {"label_status_success.txt", "label_status_error.txt", "label_status_retry.txt", "label_status_ignore.txt"};

    @PostConstruct
    public void init() throws Exception {
        // orders
        Form form = formRepository.findByName("orders");
        if(form==null) {
            form = new Form();
            form.setDb("auswertung");
            form.setName("orders");
            form.setDescription("This is the main search form.");
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
            fields.add(new FormField("shippingId", FormField.Type.TEXT.name(), 7, "", "Shipping", false, true));
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
            form.setDescription("Enter description...");
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
            form.setDescription("Enter description...");
            form.setPosition(3);
            form.setSqlStatement(IOUtils.toString(DataInitializer.class.getClassLoader().getResourceAsStream("shipping.sql")));
            form.setPrintable(true);
            form.setAccessPublic(true);
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            // parameters
            fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1, "", "From", true, false));
            fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2, "", "Until", true, false));

            FormField statusField = new FormField("status", FormField.Type.TEXT.name(), 3, "", "Status", true, false);
            statusField.setLookup(new HashSet<>(Arrays.asList("processing", "%")));
            fields.add(statusField);

            // columns
            fields.add(new FormField("orderId", FormField.Type.TEXT.name(), 4, "", "Order", false, true));
            fields.add(new FormField("weight", FormField.Type.TEXT.name(), 5, "", "Weight", false, true));
            fields.add(new FormField("company", FormField.Type.TEXT.name(), 6, "", "Company", false, true));
            fields.add(new FormField("firstname", FormField.Type.TEXT.name(), 7, "", "Firstname", false, true));
            fields.add(new FormField("lastname", FormField.Type.TEXT.name(), 8, "", "Lastname", false, true));
            fields.add(new FormField("street1", FormField.Type.TEXT.name(), 9, "", "Street", false, true));
            fields.add(new FormField("street2", FormField.Type.TEXT.name(), 10, "", "", false, true));
            fields.add(new FormField("zip", FormField.Type.TEXT.name(), 11, "", "ZIP", false, true));
            fields.add(new FormField("city", FormField.Type.TEXT.name(), 12, "", "City", false, true));
            fields.add(new FormField("country", FormField.Type.TEXT.name(), 13, "", "Country", false, true));
            fields.add(new FormField("dhlAccount", FormField.Type.TEXT.name(), 14, "", "DHL #", false, true));

            form.setFields(fields);

            formRepository.save(form);
        }


        // label status
        for(String file : labelStatusFiles) {
            String status = file.substring("label_status_".length());
            status = status.substring(0, status.length()-4);

            LineIterator it = IOUtils.lineIterator(DataInitializer.class.getClassLoader().getResourceAsStream(file), Charset.forName("UTF-8"));

            while(it.hasNext()) {
                String text = it.next().trim();
                LabelStatus labelStatuse = labelStatusRepository.findByText(text);

                if(labelStatuse==null) {
                    labelStatusRepository.save(new LabelStatus(status, text));
                }
            }
        }
    }
}
