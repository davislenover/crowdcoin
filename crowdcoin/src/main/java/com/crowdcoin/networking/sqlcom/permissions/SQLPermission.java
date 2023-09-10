package com.crowdcoin.networking.sqlcom.permissions;

public enum SQLPermission {

    ALTER("ALTER"),INSERT("INSERT"),DELETE("DELETE"),SELECT("SELECT"),UPDATE("UPDATE"),
    GRANT_OPTION("GRANT OPTION"),CREATE_USER("CREATE USER"),SHOW_DATABASES("SHOW DATABASES");

    private String queryString;

    SQLPermission(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return this.queryString;
    }
}
