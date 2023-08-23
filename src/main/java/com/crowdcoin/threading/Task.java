package com.crowdcoin.threading;

/**
 * A task of arbitrary logic that can be run on a separate Thread. Typically ran by a {@link TaskManager}. By default, an instantiated Task
 * has a priority level of {@link TaskPriority#NEUTRAL}
 */
public abstract class Task implements Runnable {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;

    /**
     * The arbitrary logic to invoke upon invoking {@link Thread#start()}
     */
    public abstract void runTask();

    /**
     * Sets the priority of the given {@link Task}
     * @param priority the given priority enum as a {@link TaskPriority} object
     */
    public void setPriority(TaskPriority priority) {
        this.taskPriority = taskPriority;
    }

    /**
     * Gets the current priority level of the given {@link Task} instance
     * @return the given {@link TaskPriority} object
     */
    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

    /**
     * Overridden method to call {@link Task#runTask()} on a new thread
     */
    public void run() {
        runTask();
    }

}
