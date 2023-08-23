package com.crowdcoin.mainBoard.table.Observe;

public enum TaskEventType implements GeneralEventType {

    THREAD_START("A new thread has started to execute"),THREAD_END("The thread that had previously started has finished executing");

    private String description;

    TaskEventType(String description) {
        this.description = description;
    }

    @Override
    public String description() {
        return this.description;
    }
}
