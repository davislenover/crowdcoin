package com.crowdcoin.networking.sqlcom.data.constraints;

/**
 * Used to omit columns from a SQL result query
 */
public interface SQLColumnConstraint extends SQLConstraint {

    /**
     * Checks if a given column name is valid to the constraint
     * @param data the given column name
     * @return true if valid, false otherwise
     */
    boolean isValid(String data);

}
