package com.ratchet.observe;

public enum TaskEventType implements GeneralEventType {

    TASK_BLOCKED("Task is in queue for execution"),TASK_START("A new task has started to execute"),TASK_END("The task that had previously started has finished executing"),
    TASK_FAILED("The task failed with an exception");

    private String description;

    TaskEventType(String description) {
        this.description = description;
    }

    @Override
    public String description() {
        return this.description;
    }
}
