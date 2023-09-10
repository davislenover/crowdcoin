package com.crowdcoin.exceptions.handler.strategies;
import javafx.stage.Stage;

public interface ExceptionStrategy<T extends Throwable> {

    void handleException(T throwable);

}
