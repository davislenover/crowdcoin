package com.crowdcoin.networking.sqlcom.data.constraints;

public interface SQLColumnConstraint {

    /**
     * Checks if a given column name is valid to the constraint
     * @param columnName the given column name
     * @return true if valid, false otherwise
     */
    boolean isValid(String columnName);

}
