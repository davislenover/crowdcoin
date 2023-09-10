package com.ratchet.observe;

public enum ModifyEventType implements GeneralEventType {
    NEW_FILTER("Construct a new filter"),NEW_ROW("Added a new row to database"),PANE_UPDATE("Updated an Interactive Pane"),ROW_MODIFIED("Modified a row in the database"),ROW_REMOVED("Removed a row in the database"),DATABASE_MODIFIED("Generic database modify event"),
    APPLIED_NEW_VIEW("Applied new set of rows to a given TableView"),NEW_COLUMN("Added a new column to a database table"),REMOVED_COLUMN("Removed a column from a database table"),ADDED_USER("Added a user to the database"),REMOVED_USER("Removed a user from the database");

    private String description;

    ModifyEventType(String s) {
        this.description = s;
    }

    @Override
    public String description() {
        return this.description;
    }
}
