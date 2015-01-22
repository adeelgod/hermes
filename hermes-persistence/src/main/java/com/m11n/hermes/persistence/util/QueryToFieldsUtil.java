package com.m11n.hermes.persistence.util;

import com.m11n.hermes.core.model.FormField;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Named;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class QueryToFieldsUtil {

    @Inject
    @Named("jdbcTemplateAuswertung")
    protected NamedParameterJdbcTemplate jdbcTemplateAuswertung;

    @Inject
    @Named("jdbcTemplateLCarb")
    protected NamedParameterJdbcTemplate jdbcTemplateLCarb;

    public List<FormField> toFields(String database, String sql) {
        final List<FormField> fields = new ArrayList<>();

        int pos = sql.toLowerCase().indexOf("where"); // TODO: probably needs improvement
        pos = pos == -1 ? sql.length() : pos;
        sql = sql.substring(0, pos) + " limit 1";

        NamedParameterJdbcTemplate jdbcTemplate = "lcarb".equals(database) ? jdbcTemplateLCarb : jdbcTemplateAuswertung;

        jdbcTemplate.query(sql, Collections.<String, Object>emptyMap(), new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ResultSetMetaData md = rs.getMetaData();
                for(int i=1; i<=md.getColumnCount(); i++) {
                    FormField field = new FormField();
                    field.setColumn(true);
                    field.setName(md.getColumnLabel(i));
                    field.setDescription(md.getColumnLabel(i));
                    field.setFieldType(getType(md, i).name());
                    field.setPosition(i);
                    fields.add(field);
                }
            }
        });

        return fields;
    }

    private FormField.Type getType(ResultSetMetaData md, int col) throws SQLException {
        switch (md.getColumnType(col)) {
            case Types.BOOLEAN:
            case Types.BIT:
                return FormField.Type.BOOLEAN;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return FormField.Type.DATETIME;
            default:
                return FormField.Type.TEXT;
        }
    }
}
