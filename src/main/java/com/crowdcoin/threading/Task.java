package com.crowdcoin.threading;

import java.util.concurrent.Callable;

/**
 * A task of arbitrary logic that can be run on a separate Thread
 * Typically ran by a {@link TaskManager}. By default, an instantiated Task has a priority level of {@link TaskPriority#NEUTRAL}
 * @param <T> the given return type of the Task
 */
public interface Task<T> extends Callable<T> {

    /**
     * Sets the priority of the given {@link Task}
     * @param priority the given priority enum as a {@link TaskPriority} object
     */
    void setPriority(TaskPriority priority);

    /**
     * Gets the current priority level of the given {@link Task} instance
     * @return the given {@link TaskPriority} object
     */
    TaskPriority getTaskPriority();

    /**
     * The arbitrary logic to invoke upon invoking {@link java.util.concurrent.ExecutorService#submit(Callable)}. One should utilize the {@link java.util.concurrent.ExecutorService}
     * class to invoke this method to run on a new thread. It should NOT be called explicitly
     */
    T runTask();

    /**
     * Overridden method to call {@link Task#runTask()} on a new thread.
     */
    default T call() throws Exception {
        return runTask();
    }

    /**
     * Gets the id of a task. The id will be returned in the first string of event data when a {@link com.crowdcoin.mainBoard.table.Observe.TaskEvent} is fired
     * in relation to the given Task
     * @return the task id as a String object
     */
    String getTaskId();

    /**
     * Sets the id of a task. If this method is not invoked, the default id is a blank String object
     */
    void setTaskId(String taskId);

}
