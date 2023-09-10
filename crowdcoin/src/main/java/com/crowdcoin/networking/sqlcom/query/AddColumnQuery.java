package com.crowdcoin.networking.sqlcom.query;

public class AddColumnQuery implements QueryBuilder {

    private String tableName;
    private String columnName;
    private String type;
    private String defaultValue;

    public AddColumnQuery(String tableName, String columnName, String type, String defaultValue) {
        this.tableName = tableName;
        this.columnName = columnName;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    @Override
    public String getQuery() {
        return "ALTER TABLE " + this.tableName + " ADD COLUMN " + this.columnName + " " + this.type + " NOT NULL DEFAULT " + this.defaultValue;
    }
}
