package com.crowdcoin.networking.sqlcom.query;

public class GetColumnDataQuery implements QueryBuilder {
    private String tableName;

    public GetColumnDataQuery(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String getQuery() {
        return QueryPrefix.getColumnData.getPrefix()+"\""+this.tableName+"\"";
    }
}
