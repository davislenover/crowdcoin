package com.ratchet.system;

import com.ratchet.threading.TaskManager;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * A class that handles Ratchet wide system functions
 */
public class RatchetSystem {
    private static Stage rootStage = null;

    /**
     * Sets the root stage used by all applicable Ratchet packages. It is imperative that this is set when using Ratchet
     * @param mainStageToSet the given JavaFX stage
     */
    public static void setRootStage(Stage mainStageToSet) {
        rootStage=mainStageToSet;
    }

    /**
     * Gets the current root stage stored by Ratchet. To set the root stage, call {@link RatchetSystem#setRootStage(Stage)}
     * @return the JavaFX root stage. Null if not set
     */
    public static Stage getRootStage() {
        return rootStage;
    }

    /**
     * Closes the application and halts any threads from accepting more {@link com.ratchet.threading.Task}s. If using Ratchet, it is recommended to call this method instead of {@link System#exit(int)}
     * @param exitCode the given exit code. The same syntax to {@link System#exit(int)}
     */
    public static void exit(int exitCode) {
        Platform.exit();
        TaskManager.closeManager();
        System.exit(exitCode);
    }

}
