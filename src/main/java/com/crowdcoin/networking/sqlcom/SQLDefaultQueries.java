package com.crowdcoin.networking.sqlcom;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
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

    public static String getGetColumnDataQuery(String tableName) {
        return getColumnData+"\""+tableName+"\"";
    }

    public static String getAll(String tableName) {
        return getAll+tableName+" ";
    }

    public static String getAllWithLimit(String tableName, int firstRow, int maxRows) {

        return getAll + tableName + " LIMIT " + firstRow + "," + maxRows;
    }

    public static String getAllWithFilterAndLimit(String tableName, String filter, int firstRow, int maxRows) {

        return getAll + tableName + filter + " LIMIT " + firstRow + "," + maxRows;
    }

    public static String getAllSpecific(String tableName, String columnNameWithData, String specificData, int maxRows) {

        return getAll + tableName + " where " + columnNameWithData + " = '" + specificData + "'" + " LIMIT " + maxRows;

    }

    public static String insertValueIntoNewRow(String tableName, List<String> columnNamesToInsertData, List<String> specificData) {

        String returnString = "INSERT INTO " + tableName + " (";

        for (int index = 0; index < columnNamesToInsertData.size(); index++) {

            if (index != columnNamesToInsertData.size()-1) {
                returnString+=columnNamesToInsertData.get(index)+",";
            } else {
                returnString+=columnNamesToInsertData.get(index)+")";
            }
        }

        returnString+=" VALUES (";

        for (int index = 0; index < specificData.size(); index++) {

            if (index != specificData.size()-1) {
                returnString+="'"+specificData.get(index)+"',";
            } else {
                returnString+="'"+specificData.get(index)+"')";
            }
        }

        return returnString;

    }

    public static String insertValuesIntoExistingRow(String tableName, List<String> columnsToChange, List<String> correspondingDataToWrite, String whereColumn, String whereDataInColumn) {

        String returnString = "UPDATE " + tableName + " SET";

        for (int index = 0; index < correspondingDataToWrite.size(); index++) {

            if (index != correspondingDataToWrite.size()-1) {
                returnString+= " " + columnsToChange.get(index) + " = " + "'" + correspondingDataToWrite.get(index) + "',";
            } else {
                returnString+= " " + columnsToChange.get(index) + " = " + "'" + correspondingDataToWrite.get(index) + "'";
            }

        }

        returnString+= " WHERE " + whereColumn + " = " + "'" + whereDataInColumn + "'";

        return returnString;

    }

    public static String insertValueIntoExistingRow(String tableName, String columnNameToInsertData, String specificData, String whereColumn, String whereDataInColumn) {

        return "UPDATE " + tableName + " SET " + columnNameToInsertData + " = " + "'" + specificData + "'" + " WHERE " + whereColumn + " = " + "'" + whereDataInColumn + "'";

    }

    public static String deleteRow(String tableName, String whereColumn, String whereDataInColumn) {
        String returnString = "DELETE FROM " + tableName + " WHERE " + whereColumn + "=" + "'" + whereDataInColumn +"'";
        return returnString;
    }

}
