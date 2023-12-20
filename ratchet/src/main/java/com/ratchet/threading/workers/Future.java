package com.ratchet.threading.workers;
import com.ratchet.observe.Observable;
import com.ratchet.observe.Observer;
import com.ratchet.observe.TaskEvent;
import com.ratchet.observe.TaskEventType;
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
    private AtomicBoolean isReady;

    public Future() {
        this.isReady = new AtomicBoolean(false);
        this.subscriptionList = new ArrayList<>();
    }

    /**
     * Checks if the future object is now ready for retrieval. This method only blocks if another Thread is accessing it concurrently but will not wait on the computation by the {@link Worker} to be completed
     * @return True if the computation is complete, false otherwise
     */
    public boolean isReady() {
        return this.isReady.get();
    }

    /**
     * Typically used by the {@link Worker} object to store it's completed computation for a given {@link com.ratchet.threading.Task}. isReady() must be false to set the given computation. This will unblock any Thread blocked in getItem().
     * This will also notify any observers on the main application UI thread ({@link Platform})
     * @param setFutureObject the computation to store
     */
    public synchronized void setItem(Object setFutureObject) {
        if (!this.isReady.get()) {
            this.futureObject = setFutureObject;
            this.isReady.set(true);
            this.notifyAll();
            Platform.runLater(() -> {
                this.notifyObservers(new TaskEvent(TaskEventType.TASK_END));
            });
        }
    }

    /**
     * Gets the completed computation from the {@link Worker}. This method will block until the computation has been completed. Note that this method is thread-safe.
     * @return the completed computation object
     */
    public synchronized Object getItem() {
        while(!this.isReady.get()) {
            try {
                this.wait();
            } catch (Exception exception) {
                // TODO
            }
        }
        return this.futureObject;
    }

    /**
     * Adds an observer to the list of observers for the given {@link Future} object. There are two ways an {@link Observer} can be notified, if the computation in the Future object has finished
     * or if the computation had already finished previously and a new {@link Observer} is added through this method. Note all notification events happen through the {@link Platform} thread.
     * All methods applicable to {@link Observable} implemented within {@link Future} are thread-safe and will block if the given {@link Future} is being called by another Thread
     * @param observer the given observer to add
     * @return true if the subscription list was changed, false otherwise
     */
    @Override
    public synchronized boolean addObserver(Observer<TaskEvent,String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            this.subscriptionList.add(observer);
            if (this.isReady()) {
                Platform.runLater(() -> {
                    this.notifyObservers(new TaskEvent(TaskEventType.TASK_END));
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
