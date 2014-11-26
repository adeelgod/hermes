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
            form = formRepository.save(form);

            List<FormField> fields = new ArrayList<>();
            fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1));
            fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2));
            //fields.add(new FormField("dummy_boolean", FormField.Type.BOOLEAN.name(), 3, "false"));
            //fields.add(new FormField("dummy_text", FormField.Type.TEXT.name(), 4, "default dummy text"));

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

                if(labelStatuse!=null) {
                    labelStatusRepository.save(new LabelStatus(status, text));
                }
            }
        }
    }
}
