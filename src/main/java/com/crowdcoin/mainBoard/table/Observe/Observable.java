package com.crowdcoin.mainBoard.table.Observe;

/**
 * Defines that a class is Observable
 * @param <T> the specified type that is observable within the class. The type can be the class itself. This means that only Observers who can observe the specified type T can be added to the subscription list within the implementing class. This is typically used to specify the type of the parameter passed through update() in Observer
 */
public interface Observable<T extends ObservableEvent<? extends GeneralEventType>> {

    /**
     * Adds an observer to a subscription list. Note that subscription list is an arbitrary term. It is up to the implementing class of this interface to define how to store observers.
     * Implementing classes should not allow duplicate Observers to avoid calling update() twice on a given Observer.
     * @param observer the given observer to add
     * @return true if the subscription list was changed as a result of invocation of this method, false otherwise
     */
    boolean addObserver(Observer<T> observer);

    /**
     * Removes an observer from a subscription list. Note that subscription list is an arbitrary term. It is up to the implementing class of this interface to define how to store observers
     * @param observer the given observer to remove
     * @return true if the subscription list was changed as a result of invocation of this method, false otherwise
     */
    boolean removeObserver(Observer<T> observer);

    /**
     * On some arbitrary logic, the Observable class will invoke this method. This will invoke the update() method found in all Observers within the subscription list
     */
    void notifyObservers(T event);

    /**
     * Removes all observers from a given Observable class
     */
    void clearObservers();

}
