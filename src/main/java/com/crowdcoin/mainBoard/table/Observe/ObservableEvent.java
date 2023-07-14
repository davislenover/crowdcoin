package com.crowdcoin.mainBoard.table.Observe;

import java.util.List;

/**
 * An event with a return type T that be observed by Observer classes when an Observable class returns said ObservableEvent
 * @param <T> the type to return by {@link ObservableEvent#getEventType()}. Must be inherited from {@link ModifyEventType}
 * @param <S> the event data type, typically used to identify the arbitrary type within a list that is returned when calling {@link ObservableEvent#getEventData()}
 */
public interface ObservableEvent<T extends GeneralEventType,S> {

    /**
     * Gets event type. Typically used to pass data that the Observer class needs to perform arbitrary logic
     * @return a type T
     */
    T getEventType();

    /**
     * Gets any data related to the event. Typically used to pass further data other than the ModifyEventType
     * @return a list of objects of type S pertaining to any other event data
     */
    List<S> getEventData();

}
