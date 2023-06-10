package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

/**
 * A class to contain a specific group of SQL operators. Classes that implement this interface should be enum's
 */
public interface FilterOperators {
    /**
     * Gets the operator object as a string (may be different from the name of the object). Typically used to insert the operator into an SQL WHERE statement
     * @return
     */
    String getOperatorString();
}
