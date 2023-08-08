package com.crowdcoin.exceptions.handler;

import com.crowdcoin.exceptions.handler.strategies.ExceptionStrategy;
import com.crowdcoin.exceptions.handler.strategies.FatalException;
import javafx.stage.Stage;

public class ExceptionGuardian {

    private static Stage rootStage;

    public static void setRootStage(Stage root) {
        rootStage = root;
    }

    public static void handleGeneralException(Throwable exception) {
        rootStage.close();
        ExceptionStrategy<Throwable> fatal = new FatalException<>();
        fatal.handleException(exception);
    }

}
