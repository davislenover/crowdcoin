package com.crowdcoin.threading;

import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.mainBoard.table.Observe.TaskEvent;
import com.crowdcoin.mainBoard.table.Observe.TaskEventType;
import javafx.application.Platform;

import java.util.*;
import java.util.concurrent.*;

/**
 * Handles ordered execution of {@link Task}s ({@link Thread}s)
 */
public class TaskManager implements Observable<TaskEvent,String>, Observer<TaskEvent,String> {
    private PriorityQueue<Task<?>> tasks;
    private List<Observer<TaskEvent,String>> subscriptionList;
    private TaskWatcher<?> currentTask;
    private Future<?> endTask;
    private TaskException failedTaskException;

    public TaskManager() {
        this.tasks = new PriorityQueue<>(new TaskRunnerComparator());
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
            this.removeObserving();
            this.currentTask = null;
            this.notifyObservers(event);
        } else if (eventType.equals(TaskEventType.TASK_FAILED)) {
            this.notifyObservers(event);
        }
    }

    // Comparator for priority queue
    private class TaskRunnerComparator implements Comparator<Task> {
        @Override
        public int compare(Task o1, Task o2) {
            // Comparison is checked by fetching priority integer from enum
            // In a priority queue, the lower number has higher priority
            int o1Priority = o1.getTaskPriority().getPriority();
            int o2Priority = o2.getTaskPriority().getPriority();

            if (o1Priority < o2Priority) {
                return -1;
            }
            if (o1Priority == o2Priority) {
                return 0;
            }
            if (o1Priority > o2Priority) {
                return 1;
            }
            return 0;
        }
    }

    // A class used to create a new thread to watch the status of an invoked thread (or task)
    private class TaskWatcher<T> implements Callable<T>,Observable<TaskEvent,String> {

        private List<Observer<TaskEvent,String>> subscriptionList;
        private Task<T> task;
        private Future<T> endTask;
        public TaskWatcher(Task<T> task) {
            this.task = task;
            this.subscriptionList = new ArrayList<>();
        }

        @Override
        public T call() throws Exception {
            // Create a new thread to execute task on
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            String taskId = this.task.getTaskId();
            // Notify observers of the TaskWatcher instance that the callable task is starting
            // Call runlater() as this event may affect UI elements thus, need to handle the event in the context of the JavaFX Application Thread
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(TaskEventType.TASK_START,taskId));
            });
            // When call() is called from submit(), newly created thread will run task
            this.endTask = executorService.submit(this.task);
            T returnValue;
            try {
                // Wait for the callable task to complete and retrieve value
                // This is done in a new thread thus the thread that called submit() will not be waiting too
                returnValue = this.endTask.get();
            } catch (ExecutionException e) {
                try {
                    throw e.getCause();
                } catch (TaskException taskException) {
                    failedTaskException = taskException;
                    Platform.runLater(() -> {
                        this.notifyObservers(new TaskEvent(TaskEventType.TASK_FAILED,taskId));
                    });
                    return null;
                } catch (Throwable ex) {
                    // TODO Error handling
                    throw new RuntimeException(ex);
                }
            } catch (InterruptedException e) {
                // TODO Error handling
                throw new RuntimeException(e);
            }
            // Once complete, notify observers the task has finished
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(TaskEventType.TASK_END,taskId));
            });
            // Return value once value from new thread has been retrieved
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
            System.out.println("TASKID: " + event.getEventData().get(0) + " EVENT: " + event.getEventType());
            for (Observer<TaskEvent,String> observer : this.subscriptionList) {
                System.out.println("UPDATE");
                observer.update(event);
            }
        }

        @Override
        public void clearObservers() {
            this.subscriptionList.clear();
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
            this.currentTask = new TaskWatcher<>(this.tasks.poll());
            // Observe the current task to check for completion
            this.currentTask.addObserver(this);
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            // Call submit to create a new thread to watch the task thread for completion
            // Set return to future object in class
            this.endTask = executorService.submit(this.currentTask);
        }
    }

    /**
     * Gets the most recently completed Task value (if any). It is strongly recommended to wait for {@link TaskEventType#TASK_END} event from
     * the given TaskManager instance before invoking this method.
     * @return the completed task value as an {@link Object}. Null if no return value or the Task failed, identified by {@link TaskEventType#TASK_FAILED}. Calling class must know the specific type of the return value.
     */
    public Object getTaskValue() {
        try {
            if (this.endTask != null) {
                return this.endTask.get();
            }
            return null;
        } catch (Exception exception) {
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


}
