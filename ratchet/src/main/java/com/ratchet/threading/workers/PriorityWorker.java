package com.ratchet.threading.workers;

import com.ratchet.threading.Task;
import com.ratchet.threading.TaskException;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class PriorityWorker extends Worker {

    private AtomicBoolean isActive;
    private Queue<TaskFuturePair> tasks;
    private threadWorker threadWorker;

    public PriorityWorker(int taskCapacity) {
        // Create priority queue with comparator from VoidTask (all types of tasks use the same default compare() method)
        this.tasks = new PriorityQueue<>(taskCapacity, new TaskFuturePair(null,null));

        this.threadWorker = new threadWorker();
        this.threadWorker.start();
    }

    /**
     * A thread-safe method which gives the corresponding {@link Worker} a {@link Task} object to execute.
     * @param task the given task to complete on a separate Thread. All {@link Task}s are executed according to their priority. When other tasks of the same priority exist with the given {@link PriorityWorker}, the order in which they are executed is non-deterministic.
     * @return Will return a {@link Future} object. A {@link Future} object is where another Thread can check if the Task computation from the given {@link Worker} has been completed.
     * Once complete, the Thread can get that computed value via the {@link Future} object
     */
    public synchronized Future performTask(Task<?> task) {
        Future returnFuture = new Future();
        TaskFuturePair pair = new TaskFuturePair(task,returnFuture);
        this.tasks.offer(pair);
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
                TaskFuturePair nextTask = this.tasks.poll();
                nextTask.getFuture().setItem(nextTask.getTask().runTask());
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
