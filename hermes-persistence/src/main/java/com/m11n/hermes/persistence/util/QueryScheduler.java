package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.persistence.AuswertungRepository;
import com.m11n.hermes.persistence.BaseRowMapper;
import com.m11n.hermes.persistence.FormRepository;
import com.m11n.hermes.persistence.IntrashipDocumentRepository;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@DependsOn("dataInitializer")
public class QueryScheduler {
    private static final Logger logger = LoggerFactory.getLogger(QueryScheduler.class);

    @Inject
    private FormRepository formRepository;

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @PostConstruct
    public void init() {
        for(Form form : formRepository.findByExecuteOnStartup(true)) {
            logger.info("+++++++++++++++++++ EXECUTING QUERY: {}", form.getName());
            query(form, false, false, Collections.<String, Object>emptyMap());
        }
    }

    public void schedule(Form form) {

    }

    public Object query(Map<String, Object> parameters) {
        Form form = formRepository.findByName(parameters.get("_form").toString());

        boolean checkFiles = parameters.get("_checkFiles")==null ? false : (Boolean)parameters.get("_checkFiles");
        boolean downloadFiles = parameters.get("_downloadFiles")==null ? false : (Boolean)parameters.get("_downloadFiles");

        return query(form, checkFiles, downloadFiles, parameters);
    }

    public Object query(final Form form, final boolean checkFiles, final boolean downloadFiles, Map<String, Object> parameters) {
        RowMapper<Map<String, Object>> mapper = new BaseRowMapper() {
            @Override
            public Map<String, Object> mapRow(ResultSet resultSet, int i) throws SQLException {
                Map<String, Object> row = new HashMap<>();
                ResultSetMetaData metaData = resultSet.getMetaData();

                for(int j=1; j<=metaData.getColumnCount(); j++) {
                    String name = getLabel(metaData, j);
                    Object value = getValue(resultSet, j);

                    row.put(name, value);
                }

                if(row.containsKey("orderId") && checkFiles) {
                    row.put("_invoiceExists", new File(resultDir + "/" + row.get("orderId") + "/invoice.pdf").exists());
                    row.put("_labelExists", new File(resultDir + "/" + row.get("orderId") + "/label.pdf").exists());

                    if(Boolean.FALSE.equals(row.get("_labelExists")) && downloadFiles) {
                        String shippingId = row.get("shippingId").toString();

                        row.put("_labelPath", intrashipDocumentRepository.findFilePath(shippingId));
                    }
                }

                return row;
            }
        };

        for(FormField field : form.getFields()) {
            if(field.getType().equals(FormField.Type.DATE.name())) {
                String value = parameters.get(field.getName()).toString();
                DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(value);
                DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                parameters.put(field.getName(), df.print(dt));
            }
        }

        String[] statements = form.getSqlStatement().split(";");

        Object result = null;

        for(String statement : statements) {
            statement = statement.trim();

            logger.info("#################### STATEMENT: {}", statement);

            if(statement.toLowerCase().startsWith("select")) {
                result = auswertungRepository.query(form.getSqlStatement(), parameters, mapper);
            } else {
                result = Collections.singletonMap("modified", auswertungRepository.update(statement, parameters));
            }
        }

        return result;
    }
}
