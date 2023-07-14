package com.crowdcoin.mainBoard.table.Observe;

public enum ModifyEventType implements GeneralEventType {
    NEW_FILTER("Construct a new filter"),NEW_ROW("Added a new row to database"),PANE_UPDATE("Updated an Interactive Pane"),ROW_MODIFIED("Modified a row in the database"),ROW_REMOVED("Removed a row in the database"),DATABASE_MODIFIED("Generic database modify event");

    private String description;

    ModifyEventType(String s) {
        this.description = s;
    }

    @Override
    public String description() {
        return this.description;
    }
}
