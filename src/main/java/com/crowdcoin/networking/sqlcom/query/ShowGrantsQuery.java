package com.crowdcoin.networking.sqlcom.query;

public class ShowGrantsQuery implements QueryBuilder {
    @Override
    public String getQuery() {
        return "SHOW GRANTS";
    }
}
