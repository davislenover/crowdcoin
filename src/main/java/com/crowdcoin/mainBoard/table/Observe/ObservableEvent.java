package com.crowdcoin.mainBoard.table.Observe;

/**
 * An event with a return type T that be observed by Observer classes when an Observable class returns said ObservableEvent
 * @param <T> the type to return by {@link ObservableEvent#getEventType()}. Must be inherited from {@link EventType}
 */
public interface ObservableEvent<T extends EventType> {

    /**
     * Gets event type. Typically used to pass data that the Observer class needs to perform arbitrary logic
     * @return a type T
     */
    T getEventType();

    /**
     * Gets any data related to the event. Typically used to pass further data other than the EventType
     * @return a String object pertaining to any other event data
     */
    String getEventData();

}
