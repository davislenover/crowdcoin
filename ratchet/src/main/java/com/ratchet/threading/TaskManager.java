package com.ratchet.threading;

import com.ratchet.observe.Observable;
import com.ratchet.observe.Observer;
import com.ratchet.observe.TaskEvent;
import com.ratchet.observe.TaskEventType;
import com.ratchet.threading.workers.Worker;
import javafx.application.Platform;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * Handles ordered execution of {@link Task}s ({@link Thread}s)
 */
public class TaskManager implements Observable<TaskEvent,String>, Observer<TaskEvent,String> {
    private PriorityBlockingQueue<Task<?>> tasks;
    private List<Observer<TaskEvent,String>> subscriptionList;
    private TaskWatcher<?> currentTask;
    private Future<?> endTask;
    private TaskException failedTaskException;

    // Create a new thread to execute task on
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Worker<?> worker = new Worker<>();

    public TaskManager() {
        this.tasks = new PriorityBlockingQueue<>();
        this.subscriptionList = Collections.synchronizedList(new ArrayList<>());
        this.currentTask = null;
        this.endTask = null;
        this.failedTaskException = null;
    }

    /**
     * Adds a {@link Task} to the TaskManager. The position in which the given Task is added depends on the {@link TaskPriority} of the {@link Task} object
     * @param taskToAdd the given task to add
     */
    public void addTask(Task<?> taskToAdd) {
        this.tasks.add(taskToAdd);
    }

    /**
     * Removes a task from the TaskManager. The given task may not be available if it has already been executed
     * @param taskToRemove the given task to remove
     */
    public void removeTask(Task<?> taskToRemove) {
        this.tasks.remove(taskToRemove);
    }

    /**
     * Clear all non-executed tasks from the given {@link TaskManager} instance
     */
    public void clearTasks() {
        this.tasks.clear();
    }

    @Override
    public boolean addObserver(Observer<TaskEvent, String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<TaskEvent, String> observer) {
        if (this.subscriptionList.contains(observer)) {
            this.subscriptionList.remove(observer);
            return true;
        }
        return false;
    }

    @Override
    public void notifyObservers(TaskEvent event) {
        for (Observer<TaskEvent,String> observer : this.subscriptionList) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }

    @Override
    public void removeObserving() {
        if (this.currentTask != null) {
            this.currentTask.removeObserver(this);
        }
    }

    @Override
    public void update(TaskEvent event) {
        TaskEventType eventType = event.getEventType();
        if (eventType.equals(TaskEventType.TASK_START)) {
            this.notifyObservers(event);
        } else if (eventType.equals(TaskEventType.TASK_END)) {
            this.currentTask = null;
            this.notifyObservers(event);
        } else if (eventType.equals(TaskEventType.TASK_FAILED)) {
            this.currentTask.closeWatcher();
            this.currentTask = null;
            this.notifyObservers(event);
        }
    }

    // A class used to create a new thread to watch the status of an invoked thread (or task)
    private class TaskWatcher<T> implements Callable<T>, Observable<TaskEvent,String> {
        // List is static to avoid concurrent modifications, only TaskManager is observing this class
        private static List<Observer<TaskEvent,String>> subscriptionList = new ArrayList<>();
        private static ExecutorService anotherService = Executors.newSingleThreadExecutor();
        private Worker<T> worker = new Worker<>();
        private Task<T> task;
        private Worker.Future<T> endTask;
        public TaskWatcher(Task<T> task, Observer<TaskEvent,String> observer) {
            this.task = task;
            if (subscriptionList.isEmpty()) {
                subscriptionList.add(observer);
            }
        }

        @Override
        public T call() throws Exception {
            String taskId = this.task.getTaskId();
            // Notify observers of the TaskWatcher instance that the callable task is starting
            // Call runlater() as this event may affect UI elements thus, need to handle the event in the context of the JavaFX Application Thread
            Platform.runLater(() -> {
                // Fire event
                this.notifyObservers(new TaskEvent(TaskEventType.TASK_START,taskId));
            });
            // call performTask(), which will add the task to the worker queue for execution on a separate Thread
            this.endTask = this.worker.performTask(this.task);

            T returnValue;
            try {
                // Wait for the  task to complete and retrieve value (i.e., the thread becomes blocked to wait for result)
                // This is done in a new thread thus the thread that called performTask() will not be waiting too
                returnValue = this.endTask.getItem();
            } catch (Exception e) {
                try {
                    // On ExecutionException, some error was thrown by the task (thread running the task)
                    // Thus throw original error
                    throw e.getCause();
                    // All Tasks will throw a TaskException on error
                } catch (TaskException taskException) {
                    // Set the TaskException to TaskManager and notify that the Task failed
                    failedTaskException = taskException;
                    Platform.runLater(() -> {
                        this.notifyObservers(new TaskEvent(TaskEventType.TASK_FAILED,taskId));
                    });
                    return null;
                } catch (Throwable ex) {
                    // TODO Error handling
                    throw new RuntimeException(ex);
                }
            }
            // Once complete (no errors), notify observers the task has finished
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(TaskEventType.TASK_END,taskId));
            });
            return returnValue;
        }

        @Override
        public boolean addObserver(Observer<TaskEvent, String> observer) {
            if (!this.subscriptionList.contains(observer)) {
                return this.subscriptionList.add(observer);
            }
            return false;
        }

        @Override
        public boolean removeObserver(Observer<TaskEvent, String> observer) {
            if (this.subscriptionList.contains(observer)) {
                this.subscriptionList.remove(observer);
                return true;
            }
            return false;
        }

        @Override
        public void notifyObservers(TaskEvent event) {
            for (Observer<TaskEvent,String> observer : this.subscriptionList) {
                observer.update(event);
            }
        }

        @Override
        public void clearObservers() {
            this.subscriptionList.clear();
        }

        public static void closeWatcher() {
            anotherService.close();
        }

    }

    /**
     * Runs the next task of highest priority in the current instance of ThreadManager. If no tasks are present or a task is currently running, invoking this method has no effect.
     * Once a Task has started {@link TaskEventType#TASK_START} is fired and once the same Task ends, {@link TaskEventType#TASK_END} is fired. Both events have the corresponding task id in the first event data String array and are fired
     * in the JavaFX application thread
     */
    public void runNextTask() {
        if (this.tasks.peek() != null && this.currentTask == null) {
            this.failedTaskException = null;
            // Create new TaskWatcher to create a new thread to watch the task thread for status
            this.currentTask = new TaskWatcher<>(this.tasks.poll(),this);
            // Call submit to create a new thread to watch the task thread for completion
            // Set return to future object in class
            this.endTask = worker.performTask(this.currentTask);
        }
    }

    /**
     * Gets the most recently completed Task value (if any). It is strongly recommended to wait for {@link TaskEventType#TASK_END} event from
     * the given TaskManager instance before invoking this method. If method is called before {@link TaskEventType#TASK_END} is fired, the calling thread will be blocked until result is returned
     * @return the completed task value as an {@link Object}. Null if no return value or the Task failed, identified by {@link TaskEventType#TASK_FAILED}. Calling class must know the specific type of the return value.
     */
    public Object getTaskValue() {
        try {
            if (this.endTask != null) {
                return this.endTask.get();
            }
            return null;
        } catch (ExecutionException exception) {
            // TODO Error handling
        } catch (InterruptedException exception) {
            // TODO Error handling
        }
        return null;
    }

    /**
     * Gets a failed Task object's {@link TaskException}. If a Task fails when invoked via {@link TaskManager#runNextTask()}, a {@link TaskException} object is stored
     * in the given {@link TaskManager} instance. Observers of the {@link TaskManager} instance will be notified of a {@link TaskEventType#TASK_FAILED} event
     * @return a {@link TaskException} exception object or null if the last Task invoked via {@link TaskManager#runNextTask()} did not throw an exception
     */
    public TaskException getFailedTaskException() {
        return this.failedTaskException;
    }

    public static void closeManager() {
        executorService.close();
        TaskWatcher.closeWatcher();
    }


}
