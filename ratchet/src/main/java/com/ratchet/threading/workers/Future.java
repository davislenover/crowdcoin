package com.ratchet.threading.workers;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A class which is responsible for waiting on {@link Worker}'s to finish a computation. {@link Worker}s will place their computed value's in this object when completed.
 */
public class Future {
    private Object futureObject;
    private AtomicBoolean isReady;

    public Future() {
        this.isReady = new AtomicBoolean(false);
    }

    /**
     * Checks if the future object is now ready for retrieval. This method only blocks if another Thread is accessing it concurrently but will not wait on the computation by the {@link Worker} to be completed
     * @return True if the computation is complete, false otherwise
     */
    public boolean isReady() {
        return this.isReady.get();
    }

    /**
     * Typically used by the {@link Worker} object to store it's completed computation for a given {@link com.ratchet.threading.Task}. isReady() must be false to set the given computation. This will unblock any Thread blocked in getItem()
     * @param setFutureObject the computation to store
     */
    public synchronized void setItem(Object setFutureObject) {
        if (!this.isReady.get()) {
            this.futureObject = setFutureObject;
            this.isReady.set(true);
            this.notifyAll();
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

}
