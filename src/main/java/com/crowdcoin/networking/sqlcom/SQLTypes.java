package com.crowdcoin.networking.sqlcom;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SQLTypes {
    public static Map<String,Class<?>> SQLToJavaType = new HashMap<>()
    {{
        put("BLOB", Byte[].class);
        put("BOOLEAN", Boolean.class);
        put("TINYINT", Integer.class);
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
        put("DOUBLE", Double.class);
    }};

}
