package com.ratchet.threading.workers;

import com.ratchet.threading.Task;

import java.util.Comparator;

/**
 * A simple data structure for storing {@link Task}/{@link Future} object pairs
 */
public class TaskFutureTuple implements Comparator<TaskFutureTuple> {

    private Task<?> task;
    private Future correspondingFuture;

    public TaskFutureTuple(Task<?> task, Future correspondingFuture) {
        this.task = task;
        this.correspondingFuture = correspondingFuture;
    }

    public Task<?> getTask() {
        return this.task;
    }

    public Future getFuture() {
        return this.correspondingFuture;
    }

    /**
     * Compares Tuples by their stored {@link Task} object {@link com.ratchet.threading.TaskPriority}
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return
     */
    @Override
    public int compare(TaskFutureTuple o1, TaskFutureTuple o2) {
        return o1.getTask().compareTo(o2.getTask());
    }
}
