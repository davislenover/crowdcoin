package com.crowdcoin.exceptions.handler.strategies;

import com.crowdcoin.exceptions.handler.window.WarningExceptionWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.application.Platform;
import javafx.stage.Stage;

public class WarningException<T extends Throwable> implements ExceptionStrategy<T> {

    private String windowName = "Warning";
    private String msg;

    public WarningException(String message) {
        this.msg = message;
    }

    @Override
    public void handleException(T throwable) {
        Stage errorStage = new Stage();
        PopWindow exceptionWindow = new WarningExceptionWindow(this.windowName,this.msg);
        try {
            exceptionWindow.start(errorStage);
        } catch (Exception e) {
            Platform.exit();
        }
    }
}
