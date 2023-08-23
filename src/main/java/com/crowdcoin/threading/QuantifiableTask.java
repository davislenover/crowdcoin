package com.crowdcoin.threading;

/**
 * A task of arbitrary logic with a return value that can be run on a separate Thread
 * @param <T> the given return type of the Task
 */
public abstract class QuantifiableTask<T> implements Task<T> {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;
    public abstract T runTask();
    public void setPriority(TaskPriority priority) {
        this.taskPriority = taskPriority;
    }

    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }
}
