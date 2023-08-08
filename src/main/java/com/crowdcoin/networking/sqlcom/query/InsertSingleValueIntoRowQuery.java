package com.crowdcoin.networking.sqlcom.query;

public class InsertSingleValueIntoRowQuery implements QueryBuilder {

    private String tableName;
    private String columnNameToInsertData;
    private String specificData;
    private String whereColumn;
    private String whereDataInColumn;

    public InsertSingleValueIntoRowQuery(String tableName, String columnNameToInsertData, String specificData, String whereColumn, String whereDataInColumn) {
        this.tableName = tableName;
        this.columnNameToInsertData = columnNameToInsertData;
        this.specificData = specificData;
        this.whereColumn = whereColumn;
        this.whereDataInColumn = whereDataInColumn;
    }

    @Override
    public String getQuery() {
        return "UPDATE " + this.tableName + " SET " + this.columnNameToInsertData + " = " + "'" + this.specificData + "'" + " WHERE " + this.whereColumn + " = " + "'" + this.whereDataInColumn + "'";
    }
}
