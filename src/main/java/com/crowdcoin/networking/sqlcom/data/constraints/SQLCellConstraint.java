package com.crowdcoin.networking.sqlcom.data.constraints;

/**
 * Used to omit rows from an SQL result query
 */
public interface SQLCellConstraint extends SQLConstraint {

    /**
     * Checks if a given cells data is valid to the constraint
     * @param data the given cell data
     * @return true if valid, false otherwise
     */
    boolean isValid(String data);

}
