package com.crowdcoin.threading;

/**
 * A task of arbitrary logic with no return value that can be run on a separate Thread
 */
public abstract class VoidTask implements Task<Void> {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;

    /**
     * Method to define arbitrary logic to execute on a new thread. Method must return a null value
     * @return null value
     */
    public abstract Void runTask();

    /**
     * Sets the priority of the given {@link VoidTask}
     * @param priority the given priority enum as a {@link TaskPriority} object
     */
    public void setPriority(TaskPriority priority) {
        this.taskPriority = taskPriority;
    }

    /**
     * Gets the current priority level of the given {@link VoidTask} instance
     * @return the given {@link TaskPriority} object
     */
    public TaskPriority getTaskPriority() {
        return this.taskPriority;
    }

}