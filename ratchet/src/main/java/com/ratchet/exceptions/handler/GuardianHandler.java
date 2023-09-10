package com.ratchet.exceptions.handler;

/**
 * GuardianHandler classes provide methods for handling specific errors. They can then be invoked by calling {@link ExceptionGuardian#handleException(Throwable)}
 * @param <T> the given Throwable type that the given class will handle
 */
public interface GuardianHandler<T extends Throwable> {

    /**
     * Handle a given exception of type T. This method may use {@link com.ratchet.exceptions.handler.strategies.ExceptionStrategy} instances to handle the given exception
     * @param exception the given exception of type T
     */
    void handleException(T exception);

}
