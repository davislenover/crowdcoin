package com.crowdcoin.networking.sqlcom.data;

import com.ratchet.observe.Observer;

/**
 * Defines if a class can return an {@link SQLQueryGroup} copy of itself to group queries
 * @param <T> the return type (which extends {@link SQLQueryGroup}) of {@link QueryGroupable#getQueryGroup()}
 */
public interface QueryGroupable<T extends SQLQueryGroup> {

    /**
     * Gets a {@link SQLQueryGroup} object from the given Groupable class. Implementing classes should only ever create ONE instance and keep that instance for subsequent calls. It is strongly recommended that if the QueryGroupable class is of {@link Observer}s,
     * then the {@link SQLQueryGroup} object should be observing the class which implements the interface
     * @return an {@link SQLQueryGroup} object. Null if fails
     */
    T getQueryGroup();

}
