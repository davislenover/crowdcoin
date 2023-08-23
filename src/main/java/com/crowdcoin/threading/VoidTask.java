package com.crowdcoin.threading;

/**
 * A task of arbitrary logic with no return value that can be run on a separate Thread
 */
public abstract class VoidTask implements Task<Void> {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;
    private String taskId = "";
    @Override
    public abstract Void runTask();
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
