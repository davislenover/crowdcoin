package com.ratchet.threading;

/**
 * A task of arbitrary logic with a return value that can be run on a separate Thread
 * @param <T> the given return type of the Task
 */
public abstract class QuantifiableTask<T> implements Task<T> {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;
    private String taskId = "";
    @Override
    public abstract T runTask() throws TaskException;
    @Override
    public void setPriority(TaskPriority priority) {
        this.taskPriority = taskPriority;
    }
    @Override
    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }
    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    @Override
    public String getTaskId() {
        return this.taskId;
    }
}
