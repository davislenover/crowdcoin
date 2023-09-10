package com.crowdcoin.networking.sqlcom.query;

public class GetColumnDataWithLimitQuery implements QueryBuilder {
    private String tableName;
    private int firstRow;
    private int maxRows;

    public GetColumnDataWithLimitQuery(String tableName, int firstRow, int maxRows) {
        this.tableName = tableName;
        this.firstRow = firstRow;
        this.maxRows = maxRows;
    }

    @Override
    public String getQuery() {
        return QueryPrefix.getall.getPrefix() + this.tableName + " LIMIT " + this.firstRow + "," + this.maxRows;
    }
}
