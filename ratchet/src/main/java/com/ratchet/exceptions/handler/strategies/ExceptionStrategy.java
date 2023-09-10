package com.ratchet.exceptions.handler.strategies;

public interface ExceptionStrategy<T extends Throwable> {

    void handleException(T throwable);

}
