package com.crowdcoin.networking.sqlcom.query;

public class AddUserQuery implements QueryBuilder {

    private String username;
    private String password;

    public AddUserQuery(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String getQuery() {
        return "CREATE USER " + "'" + this.username + "'" + "@" +"'%' IDENTIFIED BY '" + this.password + "'";
    }
}
