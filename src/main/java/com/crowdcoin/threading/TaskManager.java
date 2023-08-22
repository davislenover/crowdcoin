package com.crowdcoin.threading;

import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.mainBoard.table.Observe.ThreadEvent;
import com.crowdcoin.mainBoard.table.Observe.ThreadEventType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Handles ordered execution of {@link Task}s ({@link Thread}s)
 */
public class TaskManager implements Observable<ThreadEvent,String>, Observer<ThreadEvent,String> {

    private PriorityQueue<Task> tasks;
    private List<Observer<ThreadEvent,String>> subscriptionList;

    private checkThread currentTask;

    public TaskManager() {
        this.tasks = new PriorityQueue<>(new TaskRunnerComparator());
        this.subscriptionList = new ArrayList<>();
    }

    /**
     * Adds a {@link Task} to the TaskManager. The position in which the given Task is added depends on the {@link TaskPriority} of the {@link Task} object
     * @param taskToAdd the given task to add
     */
    public void addTask(Task taskToAdd) {
        this.tasks.add(taskToAdd);
    }

    /**
     * Removes a task from the TaskManager. The given task may not be available if it has already been executed
     * @param taskToRemove the given task to remove
     */
    public void removeTask(Task taskToRemove) {
        this.tasks.remove(taskToRemove);
    }

    /**
     * Clear all non-executed tasks from the given {@link TaskManager} instance
     */
    public void clearTasks() {
        this.tasks.clear();
    }

    @Override
    public boolean addObserver(Observer<ThreadEvent, String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ThreadEvent, String> observer) {
        if (this.subscriptionList.contains(observer)) {
            this.subscriptionList.remove(observer);
            return true;
        }
        return false;
    }

    @Override
    public void notifyObservers(ThreadEvent event) {
        for (Observer<ThreadEvent,String> observer : this.subscriptionList) {
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
    public void update(ThreadEvent event) {
        if (event.getEventType().equals(ThreadEventType.THREAD_START)) {
            this.notifyObservers(event);
        } else if (event.getEventType().equals(ThreadEventType.THREAD_END)) {
            this.removeObserving();
            this.currentTask = null;
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
    private class checkThread extends Thread implements Observable<ThreadEvent,String> {

        private List<Observer<ThreadEvent,String>> subscriptionList;
        private Runnable task;
        public checkThread(Runnable task) {
            this.task = task;
            this.subscriptionList = new ArrayList<>();
        }
        @Override
        public void run() {
            // When run is called from start(), create a new thread for the runnable task
            Thread runThread = new Thread(this.task);
            // Notify observers of the checkThread instance that the runnable task is starting
            this.notifyObservers(new ThreadEvent(ThreadEventType.THREAD_START));
            runThread.start();
            try {
                // Wait for the runnable task to complete
                // This is done in a new thread thus the thread that called start() will not be waiting too
                runThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Once complete, notify observers the task has finished
            this.notifyObservers(new ThreadEvent(ThreadEventType.THREAD_END));
        }

        @Override
        public boolean addObserver(Observer<ThreadEvent, String> observer) {
            if (!this.subscriptionList.contains(observer)) {
                return this.subscriptionList.add(observer);
            }
            return false;
        }

        @Override
        public boolean removeObserver(Observer<ThreadEvent, String> observer) {
            if (this.subscriptionList.contains(observer)) {
                this.subscriptionList.remove(observer);
                return true;
            }
            return false;
        }

        @Override
        public void notifyObservers(ThreadEvent event) {
            for (Observer<ThreadEvent,String> observer : this.subscriptionList) {
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
     */
    public void runNextTask() {
        if (this.tasks.peek() != null && this.currentTask == null) {
            this.currentTask = new checkThread(this.tasks.poll());
            // Observe the current task to check for completion
            this.currentTask.addObserver(this);
            // Call start to create a new thread to watch the task thread for completion
            this.currentTask.start();
        }
    }


}
