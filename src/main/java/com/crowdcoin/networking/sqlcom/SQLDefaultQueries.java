package com.crowdcoin.networking.sqlcom;

public class SQLDefaultQueries {

    private static String getAll = "select * from ";
    public static String informationSchemaColumnName = "COLUMN_NAME";
    public static String informationSchemaDataType = "DATA_TYPE";
    private static String getColumnData = "select * FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ";

    public static String getGetColumnDataQuery(String tableName) {
        return getColumnData+"\""+tableName+"\"";
    }

    public static String getAll(String tableName) {
        return getAll+tableName+" ";
    }

    public static String getAllWithLimit(String tableName, int firstRow, int maxRows) {
        return getAll+tableName+" LIMIT "+firstRow+","+maxRows;
    }

}
