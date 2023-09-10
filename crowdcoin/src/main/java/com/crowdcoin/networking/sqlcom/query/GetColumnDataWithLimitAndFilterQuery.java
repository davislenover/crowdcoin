package com.crowdcoin.networking.sqlcom.query;

public class GetColumnDataWithLimitAndFilterQuery implements QueryBuilder {
    private String tableName;
    private String filter;
    private int firstRow;
    private int maxRows;

    public GetColumnDataWithLimitAndFilterQuery(String tableName, String filter, int firstRow, int maxRows) {
        this.tableName = tableName;
        this.filter = filter;
        this.firstRow = firstRow;
        this.maxRows = maxRows;
    }

    @Override
    public String getQuery() {
        return QueryPrefix.getall.getPrefix() + this.tableName + this.filter + " LIMIT " + this.firstRow + "," + this.maxRows;
    }
}
