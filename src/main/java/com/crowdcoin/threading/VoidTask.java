package com.crowdcoin.threading;

import java.util.concurrent.Callable;

/**
 * A task of arbitrary logic that can be run on a separate Thread. Typically ran by a {@link TaskManager}. By default, an instantiated VoidTask
 * has a priority level of {@link TaskPriority#NEUTRAL}
 */
public abstract class VoidTask implements Task<Integer> {
    private TaskPriority taskPriority = TaskPriority.NEUTRAL;

    /**
     * The arbitrary logic to invoke upon invoking {@link Thread#start()}
     */
    public abstract void runTask();

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

    /**
     * Overridden method to call {@link VoidTask#runTask()} on a new thread
     */
    public Integer call() {
        runTask();
        return 0;
    }

}
