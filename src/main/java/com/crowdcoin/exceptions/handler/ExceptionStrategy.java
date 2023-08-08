package com.crowdcoin.exceptions.handler;
import javafx.stage.Stage;

public interface ExceptionStrategy<T extends Throwable> {

    void setRootStage(Stage rootStage);

    void handleException(T throwable);

}
