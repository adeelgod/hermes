package com.m11n.hermes.service.report;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.service.ReportService;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.util.*;

@FixMethodOrder
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/spring/applicationContext-hermes.xml"})
public class DynamicReportServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(DynamicReportServiceTest.class);

    @Inject
    private DynamicReportService dynamicReportService;

    @Before
    public void setUp() {
    }

    @Test
    public void testGenerate() throws Exception {
        Form form = new Form();
        form.setName("dynamic");
        form.setDescription("Test Report Orders (limit 100)");
        form.setDb("auswertung");
        //form.setSqlStatement("SELECT order_id AS \"unId\", invoice_id AS \"invoiceId\", Bestellung AS \"orderId\", shipping_lable AS \"shippingId\", Kunden_vorname AS \"firstname\", Kunden_name AS \"lastname\", Kunden_email AS \"email\" FROM mage_custom_order WHERE Status = \"complete\" AND Datum_Lieferung > :from AND Datum_Lieferung < :until");
        form.setSqlStatement("SELECT order_id AS \"unId\", invoice_id AS \"invoiceId\", Bestellung AS \"orderId\", shipping_lable AS \"shippingId\", Kunden_vorname AS \"firstname\", Kunden_name AS \"lastname\", Kunden_email AS \"email\" FROM mage_custom_order WHERE Status = \"complete\" LIMIT :limit");

        List<FormField> fields = new ArrayList<>();

        fields.add(new FormField("from", FormField.Type.DATETIME.name(), 1, "", "From", true, false));
        fields.add(new FormField("until", FormField.Type.DATETIME.name(), 2, "", "Until", true, false));
        fields.add(new FormField("limit", FormField.Type.NUMBER.name(), 3, "", "Limit", true, false));
        fields.add(new FormField("unId", FormField.Type.TEXT.name(), 4, "", "ID", false, true));
        fields.add(new FormField("invoiceId", FormField.Type.TEXT.name(), 5, "", "Invoice", false, true));
        fields.add(new FormField("orderId", FormField.Type.TEXT.name(), 6, "", "Order", false, true));
        fields.add(new FormField("shippingId", FormField.Type.TEXT.name(), 7, "", "Shipping", false, true));
        fields.add(new FormField("firstname", FormField.Type.TEXT.name(), 8, "", "Firstname", false, true));
        fields.add(new FormField("lastname", FormField.Type.TEXT.name(), 9, "", "Lastname", false, true));
        fields.add(new FormField("email", FormField.Type.TEXT.name(), 10, "", "Email", false, true));

        form.setFields(fields);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("from", new Date());
        parameters.put("until", new Date());
        parameters.put("limit", 100);

        dynamicReportService.generate(form, parameters, new FileOutputStream("target/dynamic.pdf"));
    }

}
