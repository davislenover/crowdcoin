package com.crowdcoin.networking.sqlcom.data.constraints;

public interface SQLConstraint {

    /**
     * Checks if a given String of data is valid
     * @param data the given data
     * @return true if valid, false otherwise
     */
    boolean isValid(String data);

}
