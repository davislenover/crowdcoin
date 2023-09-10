package com.crowdcoin.networking.sqlcom.query;

public class DeleteRowQuery implements QueryBuilder {

    private String tableName;
    private String whereColumn;
    private String whereDataInColumn;

    public DeleteRowQuery(String tableName, String whereColumn, String whereDataInColumn) {
        this.tableName = tableName;
        this.whereColumn = whereColumn;
        this.whereDataInColumn = whereDataInColumn;
    }

    @Override
    public String getQuery() {
        return "DELETE FROM " + this.tableName + " WHERE " + this.whereColumn + "=" + "'" + this.whereDataInColumn +"'";
    }
}
