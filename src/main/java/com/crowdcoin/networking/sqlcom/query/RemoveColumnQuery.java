package com.crowdcoin.networking.sqlcom.query;

public class RemoveColumnQuery implements QueryBuilder {

    private String tableName;
    private String columnToRemove;

    public RemoveColumnQuery(String tableName, String columnToRemove) {
        this.tableName = tableName;
        this.columnToRemove = columnToRemove;
    }
    @Override
    public String getQuery() {
        return "ALTER TABLE " + this.tableName + " DROP COLUMN " + this.columnToRemove;
    }
}
