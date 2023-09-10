package com.crowdcoin.exceptions.handler;

import com.crowdcoin.exceptions.handler.strategies.ExceptionStrategy;
import com.crowdcoin.exceptions.handler.strategies.FatalException;

public class GeneralExceptionHandler implements GuardianHandler<Exception> {
    @Override
    public void handleException(Exception exception) {
        ExceptionGuardian.getRootStage().close();
        ExceptionStrategy<Exception> fatal = new FatalException<>();
        fatal.handleException(exception);
    }
}
