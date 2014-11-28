package com.m11n.hermes.service.report;

import com.m11n.hermes.core.model.Form;
import com.m11n.hermes.core.model.FormField;
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.datatype.DataTypes;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.definition.datatype.DRIDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import static net.sf.dynamicreports.report.builder.DynamicReports.*;

@Component
public class DynamicReportService {
    private static final Logger logger = LoggerFactory.getLogger(DefaultReportService.class);

    @Inject
    @Named("dataSourceAuswertung")
    private DataSource dataSourceAuswertung;

    @Inject
    @Named("dataSourceLCarb")
    private DataSource dataSourceLCarb;

    public void generate(Form form, Map<String, Object> parameters, OutputStream os) {
        StyleBuilder textStyle = stl.style(DynamicReportTemplate.columnStyle).setBorder(stl.pen1Point());

        String sql = form.getSqlStatement();

        try {
            JasperReportBuilder builder = report()
                    .setTemplate(DynamicReportTemplate.reportTemplate)
                    .setColumnStyle(textStyle);

            for(FormField field : form.getFields()) {
                if(field.getColumn()) {
                    builder = builder.addColumn(col.column(field.getDescription(), field.getName(), getDataType(field.getFieldType())));
                } else if(field.getParameter()) {
                    logger.debug("################### DYNAMIC REPORT SQL REPLACE: {}", field.getName());
                    sql = sql.replaceAll(":" + field.getName(), "\\$P\\{" + field.getName() + "\\}");
                    builder = builder.addParameter(field.getName(), parameters.get(field.getName()).getClass());
                }
            }

            logger.debug("################### DYNAMIC REPORT SQL: {}", sql);

            builder = builder.title(DynamicReportTemplate.createTitleComponent(form.getDescription()))
                    .pageFooter(DynamicReportTemplate.footerComponent)
                    .setQuery(sql)
                    .setParameters(parameters);

            if("lcarb".equals(form.getDb())) {
                builder = builder.setConnection(dataSourceLCarb.getConnection());
            } else {
                builder = builder.setConnection(dataSourceAuswertung.getConnection());
            }

            builder.toJrXml(new FileOutputStream("target/dynamic.jrxml"));
            builder.toPdf(os);
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }

    private DRIDataType<?, ?> getDataType(String fieldType) {
        switch(FormField.Type.valueOf(fieldType)) {
            case BOOLEAN:
                return DataTypes.booleanType();
            case DATE:
            case DATETIME:
            case TIME:
                return DataTypes.dateType();
            case NUMBER:
                return DataTypes.integerType();
            default:
                return DataTypes.stringType();
        }
    }
}