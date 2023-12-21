package com.ratchet.threading.workers;
import com.ratchet.observe.Observable;
import com.ratchet.observe.Observer;
import com.ratchet.observe.TaskEvent;
import com.ratchet.observe.TaskEventType;
import com.ratchet.threading.TaskException;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class which is responsible for waiting on {@link Worker}'s to finish a computation. {@link Worker}s will place their computed value's in this object when completed. {@link Platform} can be notified of completion through an {@link com.ratchet.observe.ObservableEvent}
 * if an object executing within the context of the {@link Platform} thread subscribes to the given {@link Future} object.
 */
public class Future implements Observable<TaskEvent,String> {

    private List<Observer<TaskEvent,String>> subscriptionList;
    private Object futureObject;
    private TaskEventType taskStatus;
    private TaskException exception;

    public Future() {
        this.subscriptionList = new ArrayList<>();
        this.taskStatus = TaskEventType.TASK_BLOCKED;
    }

    /**
     * Checks the status of task from the Future object. This method only blocks if another Thread is accessing it concurrently but will not wait on the computation by the {@link Worker} to be completed
     * @return a {@link TaskEventType} object
     */
    public synchronized TaskEventType getStatus() {
        return this.taskStatus;
    }

    /**
     * Typically used by the {@link Worker} object to store it's completed computation for a given {@link com.ratchet.threading.Task}. {@link Future#getStatus()} must not be {@link TaskEventType#TASK_END} and {@link TaskEventType#TASK_FAILED} to set the given computation. This will unblock any Thread blocked in getItem().
     * This will also notify any observers on the main application UI thread ({@link Platform})
     * @param setFutureObject the computation to store
     */
    public synchronized void setItem(Object setFutureObject) {
        if (!this.taskStatus.equals(TaskEventType.TASK_END) && !this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
            this.futureObject = setFutureObject;
            this.taskStatus = TaskEventType.TASK_END;
            this.notifyAll();
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(this.taskStatus));
            });
        }
    }

    /**
     * Typically used by the {@link Worker} object store a failed {@link com.ratchet.threading.Task} {@link TaskException} for retrieval. {@link Future#getStatus()} must not be {@link TaskEventType#TASK_END} and {@link TaskEventType#TASK_FAILED} to set the error. This will unblock any Thread blocked in getItem() and throw the stored {@link TaskException}.
     * This will also notify any observers on the main application UI thread ({@link Platform})
     * @param exception the {@link TaskException} to Throw
     */
    public synchronized void setException(TaskException exception) {
        if (!this.taskStatus.equals(TaskEventType.TASK_END) && !this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
            this.exception = exception;
            this.taskStatus = TaskEventType.TASK_FAILED;
            this.notifyAll();
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(this.taskStatus));
            });
        }
    }

    /**
     * Sets the current status of a given Task. It is not possible to set the status to {@link TaskEventType#TASK_END} as this would cause a potential class violation, that is, task status is set to end but no result exists.
     * This method is intended for use by a Thread which is running a corresponding {@link com.ratchet.threading.Task}.
     * {@link Future#setItem(Object)} will automatically set the status to {@link TaskEventType#TASK_END} when called with an {@link Object}
     * {@link Future#setException(TaskException)} will automatically set the status to {@link TaskEventType#TASK_FAILED} when called with a {@link TaskException}
     * @param taskStatus sets the given task status as a {@link TaskEventType} object
     */
    public synchronized void setTaskStatus(TaskEventType taskStatus) {
        if (!taskStatus.equals(TaskEventType.TASK_END) && !taskStatus.equals(TaskEventType.TASK_FAILED) && !this.taskStatus.equals(TaskEventType.TASK_END) && !this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
            this.taskStatus = taskStatus;
        }
    }

    /**
     * Gets the completed computation from the {@link Worker}. This method will block until the computation has been completed. Note that this method is thread-safe.
     * @return the completed computation object
     * @throws TaskException if the corresponding Task failed. Any calls to this method under a state in which the corresponding {@link com.ratchet.threading.Task} failed will throw a {@link TaskException}. Catch the error and call {@link TaskException#getRootException()} to find root cause.
     */
    public synchronized Object getItem() throws TaskException {
        while(!this.taskStatus.equals(TaskEventType.TASK_END) && !this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
            try {
                this.wait();
            } catch (Exception exception) {
                // TODO
            }
        }
        if (!this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
            return this.futureObject;
        } else {
            throw this.exception;
        }
    }

    /**
     * Adds an observer to the list of observers for the given {@link Future} object. There are two ways an {@link Observer} can be notified, if the computation in the Future object has finished/failed
     * , if the computation had already finished/failed previously and a new {@link Observer} is added through this method. Note all notification events happen through the {@link Platform} thread.
     * All methods applicable to {@link Observable} implemented within {@link Future} are thread-safe and will block if the given {@link Future} is being called by another Thread
     * @param observer the given observer to add
     * @return true if the subscription list was changed, false otherwise
     */
    @Override
    public synchronized boolean addObserver(Observer<TaskEvent,String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            this.subscriptionList.add(observer);
            if (this.taskStatus.equals(TaskEventType.TASK_END) || this.taskStatus.equals(TaskEventType.TASK_FAILED)) {
                Platform.runLater(() -> {
                    this.notifyObservers(new TaskEvent(this.taskStatus));
                });
            }
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean removeObserver(Observer<TaskEvent,String> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public synchronized void notifyObservers(TaskEvent event) {
        for (Observer<TaskEvent,String> observer : List.copyOf(this.subscriptionList)) {
            observer.update(event);
        }
    }

    @Override
    public synchronized void clearObservers() {
        this.subscriptionList.clear();
    }
}
