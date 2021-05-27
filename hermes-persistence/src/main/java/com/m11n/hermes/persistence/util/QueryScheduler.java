package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import com.m11n.hermes.core.util.PathUtil;
import com.m11n.hermes.persistence.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.StopWatch;
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
@DependsOn("dataSourceJpa")
public class QueryScheduler {
    private static final Logger logger = LoggerFactory.getLogger(QueryScheduler.class);

    @Inject
    private FormRepository formRepository;

    @Inject
    private IntrashipDocumentRepository intrashipDocumentRepository;

    @Inject
    private AuswertungRepository auswertungRepository;

    @Inject
    private LCarbRepository lCarbRepository;

    @Value("${hermes.result.dir}")
    private String resultDir;

    @Value("${hermes.db.multiple.queries:true}")
    private boolean multipleQueries;

    // private Pattern functions = Pattern.compile("(\\s*|^)(ADDDATE|ADDTIME|CURDATE|CURRENT_DATE|CURRENT_TIME|CURRENT_TIMESTAMP|CURTIME|DATE_ADD|DATE_FORMAT|DATE_SUB|DATE|DATEDIFF|DAY|DAYNAME|DAYOFMONTH|DAYOFWEEK|DAYOFYEAR|EXTRACT|FROM_DAYS|FROM_UNIXTIME|GET_FORMAT|HOUR|LAST_DAY|LOCALTIME|LOCALTIMESTAMP|MAKEDATE|MAKETIME|MICROSECOND|MINUTE|MONTH|MONTHNAME|NOW|PERIOD_ADD|PERIOD_DIFF|QUARTER|SEC_TO_TIME|SECOND|STR_TO_DATE|SUBDATE|SUBTIME|SYSDATE|TIME_FORMAT|TIME_TO_SEC|TIME|TIMEDIFF|TIMESTAMP|TIMESTAMPADD|TIMESTAMPDIFF|TO_DAYS|UNIX_TIMESTAMP|UTC_DATE|UTC_TIME|UTC_TIMESTAMP|WEEK|WEEKDAY|WEEKOFYEAR|YEAR|YEARWEEK)(.*|$)", Pattern.CASE_INSENSITIVE);

    @PostConstruct
    public void init() {
        for(Form form : formRepository.findByExecuteOnStartup(true)) {
            query(form, false, false, Collections.<String, Object>emptyMap());
        }

        logger.info("Query optimization enabled: {}", multipleQueries);
    }

    public void schedule(Form form) {

    }

    public Object query(Map<String, Object> parameters) {
        Form form = formRepository.findByName(parameters.get("_form").toString());

        logger.debug("parameters ::" + parameters);

        boolean checkFiles = parameters.get("_checkFiles")==null ? false : (Boolean)parameters.get("_checkFiles");
        boolean downloadFiles = parameters.get("_downloadFiles")==null ? false : (Boolean)parameters.get("_downloadFiles");

        return query(form, checkFiles, downloadFiles, parameters);
    }

    public Object query(final Form form, final boolean checkFiles, final boolean downloadFiles, Map<String, Object> parameters) {
        Object result = null;

        try {
            RowMapper<Map<String, Object>> mapper = new BaseRowMapper<Map<String, Object>>() {
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
                        row.put("_invoiceExists", new File(resultDir + "/" + PathUtil.segment(row.get("orderId")+"") + "/invoice.pdf").exists());
                        row.put("_labelExists", new File(resultDir + "/" + PathUtil.segment(row.get("orderId")+"") + "/label.pdf").exists());

                        if(Boolean.FALSE.equals(row.get("_labelExists")) && downloadFiles) {
                            if(row.get("shippingId")!=null) {
                                String shippingId = row.get("shippingId").toString();

                                row.put("_labelPath", intrashipDocumentRepository.findFilePath(shippingId));
                            } else {
                                logger.warn("FILE NOT FOUND: {}", resultDir + "/" + PathUtil.segment(row.get("orderId")+"") + "/label.pdf");
                                row.put("shippingId", "");
                            }
                        }
                    }

                    return row;
                }
            };

            for(FormField field : form.getFields()) {
                if(field.getFieldType() == null) {
                    logger.error("*************************************** ORPHAN FIELD: {}", field);
                }
                //if(FormField.Type.DATE.name().equals(field.getFieldType()) || FormField.Type.DATETIME.name().equals(field.getFieldType())) {
                if(FormField.Type.DATE.name().equals(field.getFieldType()) || FormField.Type.DATETIME.name().equals(field.getFieldType())) {
                    String value = "";
                    try {
                        value = parameters.get(field.getName()).toString();
                        DateTime dt = ISODateTimeFormat.dateTime().parseDateTime(value);
                        DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                        parameters.put(field.getName(), df.print(dt));
                    } catch (Exception e) {
                        logger.error("Missing Parameter '" + field.getName() + "'. Got " + parameters);
                    }
                }
            }

            String sqlStatement = form.getSqlStatement().toLowerCase();

            logger.debug("Query ::" + sqlStatement);
            StopWatch watch = new StopWatch();
            watch.start();
            if(multipleQueries && !Boolean.TRUE.equals(form.getPrintable()) && (sqlStatement.contains("update") || sqlStatement.contains("insert") || sqlStatement.contains("delete")) ) {
                logger.debug("Execute multi-query batch: {}", form.getName());
                result = executeBatch(form, parameters);
            } else {
                result = executeSingleStep(form, parameters, mapper);
            }
            watch.stop();
            logger.debug("Query took: {} - {}", form.getName(), watch.toString());

            /**
             *
             * TODO: experimental
             *
             String[] statements = form.getSqlStatement().split(";");

             for(String statement : statements) {
             statement = statement.trim().replaceAll("\n", "").replaceAll("\r", "");

             if(!StringUtils.isEmpty(statement)) {
             if(statement.toLowerCase().startsWith("select")) {
             if("auswertung".equalsIgnoreCase(form.getDb())) {
             result = auswertungRepository.query(statement, parameters, mapper);
             } else if("lcarb".equalsIgnoreCase(form.getDb())) {
             result = lCarbRepository.query(statement, parameters, mapper);
             } else {
             logger.warn("################### DB is not set in form: {}. Setting default (auswertung).", form.getName());
             result = auswertungRepository.query(statement, parameters, mapper);
             }
             } else {
             result = Collections.singletonMap("modified", auswertungRepository.update(statement, parameters));
             }
             }
             }
             */
        } catch(Throwable t) {
            logger.error(t.toString(), t);
        }

        return result;
    }

    private Object executeSingleStep(Form form, Map<String, Object> parameters, RowMapper<Map<String, Object>> mapper) {
        Object result = null;

        String[] statements = form.getSqlStatement().split(";");

        for(String statement : statements) {
            statement = statement.trim().replaceAll("\n", " ").replaceAll("\r", "");

            if(!StringUtils.isEmpty(statement)) {
                if(statement.toLowerCase().startsWith("select")) {
                    if("auswertung".equalsIgnoreCase(form.getDb())) {
                        result = auswertungRepository.query(statement, parameters, mapper);
                    } else if("lcarb".equalsIgnoreCase(form.getDb())) {
                        result = lCarbRepository.query(statement, parameters, mapper);
                    } else {
                        logger.warn("################### DB is not set in form: {}. Setting default (auswertung).", form.getName());
                        result = auswertungRepository.query(statement, parameters, mapper);
                    }
                } else {
                    result = Collections.singletonMap("modified", auswertungRepository.update(statement, parameters));
                }
            }
        }

        return result;
    }

    // NOTE: only works as an update statement
    private Object executeBatch(Form form, Map<String, Object> parameters) {
        /*int result;

        String statement = form.getSqlStatement().replaceAll("\n", " ").replaceAll("\r", "");

        if("auswertung".equalsIgnoreCase(form.getDb())) {
            result = auswertungRepository.update(statement, parameters);
        } else if("lcarb".equalsIgnoreCase(form.getDb())) {
            result = lCarbRepository.update(statement, parameters);
        } else if("fair-shea".equalsIgnoreCase(form.getDb())) {
            result = lCarbRepository.update(statement, parameters);
        } else {
            logger.warn("################### DB is not set in form: {}. Setting default (auswertung).", form.getName());
            result = auswertungRepository.update(statement, parameters);
        }

        return Collections.singletonMap("modified", result);*/

        int result;

        String statement = form.getSqlStatement().replaceAll("\n", " ").replaceAll("\r", "");

        if("auswertung".equalsIgnoreCase(form.getDb())) {
            result = auswertungRepository.update(statement, parameters);
        } else if("lcarb".equalsIgnoreCase(form.getDb())) {
            result = lCarbRepository.update(statement, parameters);
        } else {
            logger.warn("################### DB is not set in form: {}. Setting default (auswertung).", form.getName());
            result = auswertungRepository.update(statement, parameters);
        }

        return Collections.singletonMap("modified", result);
    }
}