package com.m11n.hermes.persistence;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Map;

public abstract class BaseRowMapper implements RowMapper<Map<String, Object>> {
    protected Object getValue(ResultSet resultSet, int i) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();

        Object value = null;
        switch(metaData.getColumnType(i)) {
            case Types.BOOLEAN:
                value = resultSet.getBoolean(i);
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
                value = resultSet.getString(i);
                break;
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.INTEGER:
                value = resultSet.getInt(i);
                break;
            case Types.DECIMAL:
                value = resultSet.getDouble(i);
                break;
            case Types.BIGINT:
                value = resultSet.getBigDecimal(i);
                break;
            case Types.DATE:
                value = resultSet.getDate(i);
                break;
        }
        
        return value;
    }

    protected String getLabel(ResultSetMetaData metaData, int i) throws SQLException {
        String name = metaData.getColumnLabel(i);

        if(StringUtils.isEmpty(name)) {
            name = metaData.getColumnName(i);
        }

        return name;
    }
}
