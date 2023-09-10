package com.ratchet.exceptions.handler;

import com.ratchet.exceptions.handler.strategies.ExceptionStrategy;
import com.ratchet.exceptions.handler.strategies.FatalException;

public class GeneralExceptionHandler implements GuardianHandler<Exception> {
    @Override
    public void handleException(Exception exception) {
        ExceptionGuardian.getRootStage().close();
        ExceptionStrategy<Exception> fatal = new FatalException<>();
        fatal.handleException(exception);
    }
}
