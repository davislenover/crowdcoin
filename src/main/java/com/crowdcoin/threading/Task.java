package com.crowdcoin.threading;

import java.util.concurrent.Callable;

public interface Task<T> extends Callable<T> {

    /**
     * Sets the priority of the given {@link VoidTask}
     * @param priority the given priority enum as a {@link TaskPriority} object
     */
    void setPriority(TaskPriority priority);

    /**
     * Gets the current priority level of the given {@link VoidTask} instance
     * @return the given {@link TaskPriority} object
     */
    TaskPriority getTaskPriority();

}
