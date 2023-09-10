package com.crowdcoin.networking.sqlcom.query;

public class GetColumnDataSpecificQuery implements QueryBuilder {
    private String tableName;
    private String columnNameWithData;
    private String specificData;
    private int maxRows;

    public GetColumnDataSpecificQuery(String tableName, String columnNameWithData, String specificData, int maxRows) {
        this.tableName = tableName;
        this.columnNameWithData = columnNameWithData;
        this.specificData = specificData;
        this.maxRows = maxRows;
    }

    @Override
    public String getQuery() {
        return QueryPrefix.getall.getPrefix() + this.tableName + " WHERE " + this.columnNameWithData + " = '" + this.specificData + "'" + " LIMIT " + this.maxRows;
    }
}
