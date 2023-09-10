package com.ratchet.exceptions.handler.strategies;

import com.ratchet.exceptions.handler.window.FatalExceptionWindow;
import com.ratchet.window.PopWindow;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FatalException<T extends Throwable> implements ExceptionStrategy<T>{
    private String windowName = "Fatal Exception";

    @Override
    public void handleException(T throwable) {
        Stage errorStage = new Stage();
        PopWindow exceptionWindow = new FatalExceptionWindow(this.windowName,throwable);
        try {
            exceptionWindow.start(errorStage);
        } catch (Exception e) {
            Platform.exit();
        }
    }
}
