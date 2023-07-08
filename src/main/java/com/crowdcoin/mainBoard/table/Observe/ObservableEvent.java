package com.crowdcoin.mainBoard.table.Observe;

/**
 * An event with a return type T that be observed by Observer classes when an Observable class returns said ObservableEvent
 * @param <T> the type to return by {@link ObservableEvent#getEventData()}
 */
public interface ObservableEvent<T> {

    /**
     * Gets event info. Typically used to pass data that the Observer class needs to perform arbitary logic
     * @return a type T
     */
    T getEventData();

}
