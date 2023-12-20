package com.ratchet.threading.workers;

import com.ratchet.threading.Task;
import com.ratchet.threading.TaskException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker<T> extends Thread {

    private AtomicBoolean isActive;
    private Queue<Task<T>> tasks;
    private Queue<Future<T>> futures;

    public Worker() {
        this.isActive = new AtomicBoolean();
        this.isActive.set(true);
        this.tasks = new LinkedList<>();
        this.futures = new LinkedList<>();
        this.start();
    }

    public synchronized Future<T> performTask(Task<T> task) {
        this.tasks.offer(task);
        Future<T> returnFuture = new Future<>();
        futures.offer(returnFuture);
        this.notify();
        return returnFuture;

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
            try {
                this.futures.poll().setItem(this.tasks.poll().runTask());
            } catch (TaskException | NullPointerException e) {
                // TODO
            }
        }
    }

}
