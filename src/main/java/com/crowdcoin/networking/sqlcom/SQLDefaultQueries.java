package com.crowdcoin.networking.sqlcom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SQLDefaultQueries {

    private static String getAll = "select * from ";
    public static String informationSchemaColumnName = "COLUMN_NAME";
    public static String informationSchemaDataType = "DATA_TYPE";
    public static String informationSchemaOrdinalPosition = "ORDINAL_POSITION";
    private static String getColumnData = "select * FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ";

    public static Map<String,Class<?>> SQLToJavaType = new HashMap<>()
    {{
        put("BLOB", Byte[].class);
        put("BOOLEAN", Boolean.class);
        put("BYTE", Byte[].class);
        put("CHAR", String.class);
        put("CHARACTER", String.class);
        put("CHARACTER VARYING", String.class);
        put("CLOB", Byte[].class);
        put("DATETIME", Timestamp.class);
        put("DECIMAL", BigDecimal.class);
        put("NUMERIC", BigDecimal.class);
        put("DEC", BigDecimal.class);
        put("FLOAT", Double.class);
        put("DOUBLE PRECISION", Double.class);
        put("INT8", Long.class);
        put("INTEGER", Integer.class);
        put("INT", Integer.class);
        put("LVARCHAR", String.class);
        put("MONEY", BigDecimal.class);
        put("NCHAR", String.class);
        put("NVARCHAR", String.class);
        put("SERIAL", Integer.class);
        put("SERIAL8", Integer.class);
        put("SMALLFLOAT", Float.class);
        put("SMALLINT", Short.class);
        put("TEXT", String.class);
        put("VARCHAR", String.class);
    }};

    public static String getGetColumnDataQuery(String tableName) {
        return getColumnData+"\""+tableName+"\"";
    }

    public static String getAll(String tableName) {
        return getAll+tableName+" ";
    }

    public static String getAllWithLimit(String tableName, int firstRow, int maxRows) {

        return getAll+tableName+" LIMIT "+firstRow+","+maxRows;
    }

    public static String getAllSpecific(String tableName, String columnNameWithData, String specificData, int maxRows) {

        return getAll+tableName+" where " + columnNameWithData + " = '" + specificData + "'" + " LIMIT " + maxRows;

    }

}
