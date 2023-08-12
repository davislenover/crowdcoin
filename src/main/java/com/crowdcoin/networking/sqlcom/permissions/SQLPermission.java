package com.crowdcoin.networking.sqlcom.permissions;

public enum SQLPermission {

    ALTER("ALTER"),INSERT("INSERT"),DELETE("DELETE"),SELECT("SELECT"),UPDATE("UPDATE"),GRANT_OPTION("GRANT OPTION");

    private String queryString;

    SQLPermission(String queryString) {
        this.queryString = queryString;
    }

    public String getQueryString() {
        return this.queryString;
    }
}
