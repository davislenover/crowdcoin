package com.crowdcoin.networking.sqlcom.data.constraints;

public interface SQLColumnConstraint extends SQLConstraint {

    /**
     * Checks if a given column name is valid to the constraint
     * @param data the given column name
     * @return true if valid, false otherwise
     */
    boolean isValid(String data);

}
