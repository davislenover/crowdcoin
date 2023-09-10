package com.crowdcoin.exceptions.network;

// NullConnectionException occurs when the connection passed from SQLConnection class is null
public class NullConnectionException extends Exception {
    public NullConnectionException() {
        super("NullConnectionException -> The connection object from SQLConnection class is null");
    }
}
