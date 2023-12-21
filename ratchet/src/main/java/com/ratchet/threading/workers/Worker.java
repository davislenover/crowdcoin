package com.ratchet.threading.workers;

import com.ratchet.observe.TaskEventType;
import com.ratchet.threading.Task;
import com.ratchet.threading.TaskException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A {@link ThreadingWorker} class which executes {@link Task}s in an arbitrary order
 */
public class Worker implements ThreadingWorker {

    private AtomicBoolean isActive;
    private Queue<TaskFutureTuple> tasks;
    private threadWorker threadWorker;

    /**
     * Create a new active {@link Worker}
     */
    public Worker() {
        this.isActive = new AtomicBoolean();
        this.isActive.set(true);
        this.tasks = new LinkedList<>();

        this.threadWorker = new threadWorker();
        this.threadWorker.start();

    }

    /**
     * A thread-safe method which gives the corresponding {@link Worker} a {@link Task} object to execute.
     * @param task the given task to complete on a separate Thread. All {@link Task}s given will be executed sequentially in the order they were received.
     * @return Will return a {@link Future} object. A {@link Future} object is where another Thread can check if the Task computation from the given {@link Worker} has been completed. The {@link Future#getFutureId()} is the same as the given {@link Task#getTaskId()} at the time of method call
     * Once complete, the Thread can get that computed value via the {@link Future} object
     */
    public synchronized Future performTask(Task<?> task) {
        Future returnFuture = new Future(task.getTaskId());
        TaskFutureTuple pair = new TaskFutureTuple(task,returnFuture);
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

    private void doNextTask() {
        TaskFutureTuple nextTask = null;
        synchronized (this) {
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
                nextTask = this.tasks.poll();
            }

        }

        // Exit monitor to allow other threads to add tasks to queue

        if (nextTask != null) {
            try {
                nextTask.getFuture().setTaskStatus(TaskEventType.TASK_START);
                // Perform the task from the queue and place result in corresponding futures
                nextTask.getFuture().setItem(nextTask.getTask().runTask());
            } catch (TaskException e) {
                nextTask.getFuture().setException(e);
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
