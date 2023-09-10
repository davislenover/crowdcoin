package com.ratchet.observe;

public enum QueryEventType implements GeneralEventType {

    QUERY_START("Query has been submitted, awaiting response"),QUERY_END("Query completed"),QUERY_FAILED("The Query failed");

    private String description;
    QueryEventType(String description) {
        this.description = description;
    }

    @Override
    public String description() {
        return this.description;
    }
}
