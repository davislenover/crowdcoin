package com.crowdcoin.exceptions.handler;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class FatalException<T extends Throwable> implements ExceptionStrategy<T>{
    private Stage rootStage;
    private String windowName = "Fatal Exception";

    public FatalException(Stage rootStage) {
        this.rootStage = rootStage;
    }

    @Override
    public void setRootStage(Stage rootStage) {
        this.rootStage = rootStage;
    }

    @Override
    public void handleException(T throwable) {
        this.rootStage.close();

        Stage errorStage = new Stage();
        FatalExceptionWindow exceptionWindow = new FatalExceptionWindow(this.windowName,throwable);
        try {
            exceptionWindow.start(errorStage);
        } catch (Exception e) {
            Platform.exit();
        }

    }
}
