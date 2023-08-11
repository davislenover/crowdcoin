package com.crowdcoin.networking.sqlcom.data;

/**
 * Defines if a class can return an {@link SQLQueryGroup} copy of itself to group queries
 * @param <T> the return type (which extends {@link SQLQueryGroup}) of {@link QueryGroupable#getQueryGroup()}
 */
public interface QueryGroupable<T extends SQLQueryGroup> {

    /**
     * Gets a {@link SQLQueryGroup} object from the given Groupable class. This will essentially clone the given class
     * @return an {@link SQLQueryGroup} object. Null if fails
     */
    T getQueryGroup();

}
