package com.crowdcoin.threading;

public class TaskException extends Exception {
    private Exception rootException;
    public TaskException(Exception rootException) {
        this.rootException = rootException;
    }
    public Exception getRootException() {
        return this.rootException;
    }

}
