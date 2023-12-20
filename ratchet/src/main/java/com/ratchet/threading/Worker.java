package com.ratchet.threading;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker<T> extends Thread {

    private AtomicBoolean isActive;
    private Queue<Task<T>> tasks;

    public Worker() {
        this.isActive.set(true);
        this.tasks = new PriorityQueue<>();
    }

    public synchronized void performTask(Task<T> task) {
        this.tasks.add(task);
        this.notify();
    }

    public synchronized void stopWorker() {
        this.isActive.set(false);
        this.notify();
    }

    public void run() {
        while(this.isActive.get()) {
            doNextTask();
        }
    }

    private synchronized void doNextTask() {
        while(this.tasks.isEmpty() && this.isActive.get()) {
            try {
                this.wait();
            } catch (Exception exception) {
                // TODO
            }
        }
        if (this.isActive.get()) {

        }
    }

}
