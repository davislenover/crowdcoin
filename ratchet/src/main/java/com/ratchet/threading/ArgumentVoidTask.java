package com.ratchet.threading;

/**
 * Allows for Objects to be passed into tasks
 * @param <T> the type of the object to be passed
 */
public abstract class ArgumentVoidTask<T> extends VoidTask {
    private T objectToHold = null;

    /**
     * Sets an Object to hold. This object can be used within the task by calling {@link ArgumentVoidTask#getObjectHeld()}
     * @param objectToHold the given Object to hold
     */
    public void setObjectToHold(T objectToHold) {
        this.objectToHold = objectToHold;
    }

    /**
     * Gets the held Object. An object can be stored for use within the task by calling {@link ArgumentVoidTask#setObjectToHold(Object)}
     * @return the object held. It's type defined by the parameter T. Null if no value was set
     */
    public T getObjectHeld() {
        return this.objectToHold;
    }
}
