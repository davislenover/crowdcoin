package com.ratchet.threading.workers;

import com.ratchet.threading.Task;

/**
 * An interface which is used to define classes that process {@link Task} objects on a separate Thread
 */
public interface ThreadingWorker {

    /**
     * A method which gives the corresponding {@link ThreadingWorker} a {@link Task} object to execute. The implementation should be thread-safe.
     * @param task the given task to complete on a separate Thread. The order of which tasks are executed is up to the implementation.
     * @return Will return a {@link Future} object. A {@link Future} object is where another Thread can check if the Task computation from the given {@link ThreadingWorker} has been completed.
     * Once complete, the Thread can get that computed value via the {@link Future} object
     */
    public Future performTask(Task<?> task);

    /**
     * A method to stop the given {@link ThreadingWorker}. Once a worker is stopped, no further Tasks will execute. A worker cannot be re-activated
     */
    public void stopWorker();

}
