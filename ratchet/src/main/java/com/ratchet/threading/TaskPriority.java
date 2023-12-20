package com.ratchet.threading;

/**
 * Defines {@link VoidTask} object priority
 */
public enum TaskPriority {

    LOW(1),HIGH(0);

    private final int priority;

    TaskPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }

}
