package com.crowdcoin.exceptions.columnContainer;

public class NoColumnsException extends Exception {

    public NoColumnsException() {

        super("No columns exist within the given ColumnContainer instance");

    }

}
