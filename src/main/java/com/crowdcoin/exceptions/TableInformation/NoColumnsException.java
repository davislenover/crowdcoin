package com.crowdcoin.exceptions.TableInformation;

public class NoColumnsException extends Exception {

    public NoColumnsException() {

        super("No columns exist within the given TableInformation instance");

    }

}
