package com.crowdcoin.threading;

/**
 * Defines {@link VoidTask} object priority
 */
public enum TaskPriority {

    NEGLIGIBLE(10),LOW(9),MINOR(8),MODERATE(7),NEUTRAL(6),IMPORTANT(5),HIGH(4),CRITICAL(3),URGENT(2),VITAL(1);

    private final int priority;

    TaskPriority(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return this.priority;
    }
}
