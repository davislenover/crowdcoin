package com.crowdcoin.mainBoard.table.Observe;

/**
 * Defines that a class can observe a specific type of class.
 * @param <T> the type of the ObservableEvent
 */
public interface Observer<T extends ObservableEvent<?>> {

    /**
     * Stops a class from observing all it's observing. This typically involves the class keeping track of what it's observing and asking those Observable classes to remove the corresponding observer from their observer storage mechanism
     */
    void removeObserving();

    /**
     * This method is invoked by an Observable class. Typically used to notify classes of changes.
     * @param event given the type, this event is passed through by the Observable class. Typically used to pass along event details of the update
     */
    void update(T event);

}
