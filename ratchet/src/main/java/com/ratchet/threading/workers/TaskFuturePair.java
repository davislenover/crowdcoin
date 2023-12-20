package com.ratchet.threading.workers;

import com.ratchet.threading.Task;

import java.util.Comparator;

// Package-private class. Data structure for Workers to keep track of Task objects and their corresponding Future objects
public class TaskFuturePair implements Comparator<TaskFuturePair> {

    private Task<?> task;
    private Future correspondingFuture;

    public TaskFuturePair(Task<?> task, Future correspondingFuture) {
        this.task = task;
        this.correspondingFuture = correspondingFuture;
    }

    public Task<?> getTask() {
        return this.getTask();
    }

    public Future getFuture() {
        return this.getFuture();
    }

    @Override
    public int compare(TaskFuturePair o1, TaskFuturePair o2) {
        return o1.getTask().compareTo(o2.getTask());
    }
}
