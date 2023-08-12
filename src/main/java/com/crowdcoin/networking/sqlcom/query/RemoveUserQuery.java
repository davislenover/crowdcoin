package com.crowdcoin.networking.sqlcom.query;

public class RemoveUserQuery implements QueryBuilder {

    private String userName;

    public RemoveUserQuery(String userName) {
        this.userName = userName;
    }

    @Override
    public String getQuery() {
        return "DROP USER " +  "'" + this.userName + "'" + "@'%'";
    }
}
