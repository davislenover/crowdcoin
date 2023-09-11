package com.ratchet.exceptions.handler;
import com.ratchet.system.RatchetSystem;
import javafx.stage.Stage;

public class ExceptionGuardian<T extends Throwable> {
    private GuardianHandler<T> handler;

    /**
     * Creates a new ExceptionGuardian instance with a given handler
     * @param handler a GuardianHandler which handles a specific {@link Throwable} class
     */
    public ExceptionGuardian(GuardianHandler<T> handler) {
        this.handler = handler;
    }

    /**
     * Gets the root Stage object (if set) from {@link RatchetSystem#getRootStage()}
     * @return the root Stage object, Null if not already set
     */
    public static Stage getRootStage() {
        return RatchetSystem.getRootStage();
    }

    /**
     * Handle a speicifc {@link Throwable} with the given {@link GuardianHandler}. Both the GuardianHandler and ExceptionGuardian must have the same {@link Throwable} parameterized type T
     * @param throwable the given Throwable
     */
    public void handleException(T throwable) {
        handler.handleException(throwable);
    }

}
