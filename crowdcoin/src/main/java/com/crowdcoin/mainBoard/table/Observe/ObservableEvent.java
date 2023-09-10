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

    /**
     * Gets if an {@link ObservableEvent} is a broadcast event. This means all observers should perform some additional logic in reaction to it (i.e., all Tabs should refresh, all SQLTables should update, etc)
     * @return true if the event is a broadcast event, false otherwise
     */
    boolean isBroadcast();

    /**
     * Sets if an {@link ObservableEvent} is a broadcast event. This means all observers should perform some additional logic in reaction to it (i.e., all Tabs should refresh, all SQLTables should update, etc).
     * By default, if this method is not invoked, then {@link ObservableEvent#isBroadcast()} should return false
     */
    void setBroadcast(boolean isBroadcast);

}
