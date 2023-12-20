package com.ratchet.threading.workers;

import com.ratchet.threading.Task;
import com.ratchet.threading.TaskException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Worker<T> {

    private AtomicBoolean isActive;
    private Queue<Task<T>> tasks;
    private Queue<Future<T>> futures;
    private threadWorker threadWorker;

    /**
     * Create a new active {@link Worker}
     */
    public Worker() {
        this.isActive = new AtomicBoolean();
        this.isActive.set(true);
        this.tasks = new LinkedList<>();
        this.futures = new LinkedList<>();

        this.threadWorker = new threadWorker();
        this.threadWorker.start();

    }

    /**
     * A thread-safe method which gives the corresponding {@link Worker} a {@link Task} object to execute.
     * @param task the given task to complete on a separate Thread. All {@link Task}s given will be executed sequentially in the order they were received.
     * @return Will return a {@link Future} object. A {@link Future} object is where another Thread can check if the Task computation from the given {@link Worker} has been completed.
     * Once complete, the Thread can get that computed value via the {@link Future} object
     */
    public synchronized Future<T> performTask(Task<T> task) {
        this.tasks.offer(task);
        Future<T> returnFuture = new Future<>();
        futures.offer(returnFuture);
        this.notify();
        return returnFuture;
    }

    /**
     * A thread-safe method to stop the given {@link Worker}. Once a worker is stopped, no further Tasks will execute. A worker cannot be re-activated
     */
    public synchronized void stopWorker() {
        this.isActive.set(false);
        this.notify();
    }

    private synchronized void doNextTask() {
        // Check if there are tasks and that the worker is currently active
        while(this.tasks.isEmpty() && this.isActive.get()) {
            try {
                // If not, block until a task is added or worker becomes in-active
                this.wait();
            } catch (Exception exception) {
                // TODO
            }
        }
        // Check if signal was for a new task
        if (this.isActive.get()) {
            try {
                // Perform the task from the queue and place result in corresponding futures
                this.futures.poll().setItem(this.tasks.poll().runTask());
            } catch (TaskException | NullPointerException e) {
                // TODO
            }
        }
    }

    // Class which runs on a separate thread, continuously attempting to do tasks in the queue if the worker is active
    private class threadWorker extends Thread {
        public void run() {
            while(isActive.get()) {
                doNextTask();
            }
        }
    }

}
