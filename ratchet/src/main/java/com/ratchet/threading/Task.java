package com.ratchet.threading;

import com.ratchet.observe.TaskEvent;

import java.util.Comparator;
import java.util.concurrent.Callable;

/**
 * A task of arbitrary logic that can be run on a separate Thread
 * Typically ran by a {@link TaskManager}. By default, an instantiated Task has a priority level of {@link TaskPriority#LOW}
 * @param <T> the given return type of the Task
 */
public interface Task<T> extends Callable<T>, Comparator<Task>, Comparable<Task> {

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
    T runTask() throws TaskException;

    /**
     * Overridden method to call {@link Task#runTask()} on a new thread.
     */
    default T call() throws Exception {
        return runTask();
    }

    /**
     * Gets the id of a task. The id will be returned in the first string of event data when a {@link TaskEvent} is fired
     * in relation to the given Task
     * @return the task id as a String object
     */
    String getTaskId();

    /**
     * Sets the id of a task. If this method is not invoked, the default id is a blank String object
     */
    void setTaskId(String taskId);

    @Override
    default int compare(Task o1, Task o2) {
        // Comparison is checked by fetching priority integer from enum
        // In a priority queue, the lower number has higher priority
        int o1Priority = o1.getTaskPriority().getPriority();
        int o2Priority = o2.getTaskPriority().getPriority();

        if (o1Priority < o2Priority) {
            return -1;
        }
        if (o1Priority == o2Priority) {
            return 0;
        }
        if (o1Priority > o2Priority) {
            return 1;
        }
        return 0;
    }

    @Override
    default int compareTo(Task o) {
        return this.compare(this,o);
    }

}
