package com.crowdcoin.mainBoard.table;

/**
 * Defines that a class can observe a specific type of class.
 * @param <T> the type of class
 */
public interface Observer<T> {

    /**
     * This method is invoked by an Observable class. Typically used to notify classes of changes.
     * @param passThroughObject given the type, this object is passed through by the Observable class. Typically used to pass along event details of the update
     */
    void update(T passThroughObject);

}
