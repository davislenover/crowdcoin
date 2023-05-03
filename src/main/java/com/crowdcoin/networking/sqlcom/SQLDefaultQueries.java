package com.crowdcoin.networking.sqlcom;

public class SQLDefaultQueries {

    public static String getColumnNames = "select * from coindata";
    public static String informationSchemaColumnName = "COLUMN_NAME";
    public static String informationSchemaDataType = "DATA_TYPE";
    private static String getColumnData = "select * FROM INFORMATION_SCHEMA.COLUMNS where TABLE_NAME = ";

    public static String getGetColumnDataQuery(String tableName) {
        return getColumnData+"\""+tableName+"\"";
    }

}
