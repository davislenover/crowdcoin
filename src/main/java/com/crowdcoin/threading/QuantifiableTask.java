package com.crowdcoin.threading;

import java.util.concurrent.Callable;

public abstract class QuantifiableTask<T> implements Task<T> {

    private TaskPriority taskPriority = TaskPriority.NEUTRAL;

    public abstract T runTask();

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

    @Override
    public T call() throws Exception {
        return runTask();
    }
}
