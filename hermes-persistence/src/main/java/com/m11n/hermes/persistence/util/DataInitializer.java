package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.FormRepository;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer {
    @Inject
    private FormRepository formRepository;

    @PostConstruct
    public void init() {
        Form form = new Form();
        form.setName("orders");
        form.setSqlStatement("select * from orders");
        form = formRepository.save(form);

        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("from", FormField.Type.DATE.name(), 1));
        fields.add(new FormField("until", FormField.Type.DATE.name(), 2));
        fields.add(new FormField("dummy_boolean", FormField.Type.BOOLEAN.name(), 3));
        fields.add(new FormField("dummy_text", FormField.Type.TEXT.name(), 4));

        form.setFields(fields);

        formRepository.save(form);
    }
}
