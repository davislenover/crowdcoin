package com.crowdcoin.threading;

public abstract class TaskRunner implements Runnable {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;

    abstract void runTask();

    public void setPriority(TaskPriority priority) {
        this.taskPriority = taskPriority;
    }

    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

    public void run() {
        runTask();
    }

}
