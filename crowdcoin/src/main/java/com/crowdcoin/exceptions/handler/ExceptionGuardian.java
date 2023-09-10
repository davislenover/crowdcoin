package com.crowdcoin.exceptions.handler;
import javafx.stage.Stage;

import java.sql.SQLException;

public class ExceptionGuardian<T extends Throwable> {

    private static Stage rootStage = null;
    private GuardianHandler<T> handler;

    /**
     * Creates a new ExceptionGuardian instance with a given handler
     * @param handler a GuardianHandler which handles a specific {@link Throwable} class
     */
    public ExceptionGuardian(GuardianHandler<T> handler) {
        this.handler = handler;
    }

    /**
     * Sets the root stage for all ExceptionGuardian instances. This root scene may be closed when certain exceptions occur
     * @param root the given root scene
     */
    public static void setRootStage(Stage root) {
        rootStage = root;
    }

    /**
     * Gets the root Stage object (if set)
     * @return the root Stage object, null if not already set
     */
    public static Stage getRootStage() {
        return rootStage;
    }

    /**
     * Handle a speicifc {@link Throwable} with the given {@link GuardianHandler}. Both the GuardianHandler and ExceptionGuardian must have the same {@link Throwable} parameterized type T
     * @param throwable the given Throwable
     */
    public void handleException(T throwable) {
        handler.handleException(throwable);
    }

}
