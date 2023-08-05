package com.crowdcoin.networking.sqlcom;

/**
 * An enum class holding types for creating columns in SQL Tables
 */
public enum SQLColumnType {

    INT("INT"),DOUBLE("DOUBLE"),VARCHAR_45("VARCHAR(45)");

    private String queryString;

    SQLColumnType(String queryString) {
        this.queryString = queryString;
    }

    /**
     * Gets the type for query string construction
     * @return the type as a String
     */
    public String getQueryString() {
        return this.queryString;
    }
}
