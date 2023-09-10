package com.crowdcoin.exceptions.network;

public class FailedQueryException extends Exception {

    // rootException keeps the root cause of the failed query on hand if needed
    public Exception rootException;

    public FailedQueryException(String failedQuery, Exception rException) {
        super("FailedQueryException -> " + "'" + failedQuery + "'" + "failed to execute");
        this.rootException = rException;
    }
}
