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
        form.setSqlStatement("SELECT order_id as \"invoiceId\", Bestellung as \"orderId\", shipping_lable as \"shippingId\" FROM mage_custom_order WHERE Status = \"complete\" and Datum_Lieferung > :from and Datum_Lieferung < :until");
        form = formRepository.save(form);

        List<FormField> fields = new ArrayList<>();
        fields.add(new FormField("from", FormField.Type.DATE.name(), 1));
        fields.add(new FormField("until", FormField.Type.DATE.name(), 2));
        fields.add(new FormField("dummy_boolean", FormField.Type.BOOLEAN.name(), 3, "false"));
        fields.add(new FormField("dummy_text", FormField.Type.TEXT.name(), 4, "default dummy text"));

        form.setFields(fields);

        formRepository.save(form);
    }
}
