package com.crowdcoin.FXTools;

import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Acts as a container for JavaFX stages. Provides ordered distribution of Scenes
 */
public class StageManager {

    private static HashMap<PopWindow,Stage> stageMap = new HashMap<>();

    /**
     * Gets the corresponding JavaFX Stage for a PopWindow. If the Object does not have a corresponding Stage, a new one is created, stored and returned.
     * Classes should use this method instead of instantiating their own Stage. If this method is called with a PopWindow that is equivalent (via equals() and hashcode()) to a PopWindow in StageManager, then the stored window is closed
     * ({@link PopWindow#closeWindow()}) and a new Stage is returned
     * @param referenceObject the given PopWindow to get a Stage for
     * @return a JavaFX Stage object
     */
    public static Stage getStage(PopWindow referenceObject) {

        if (stageMap.containsKey(referenceObject)) {
            // It's possible that a user click on a button to open a given window more than once
            // Technically, the windows are the same, both equals() and hashcode() will say they are
            // Thus, if the key does exist, close the current window and add the "same" window
            getStoredKey(referenceObject).closeWindow();
            stageMap.put(referenceObject,new Stage());
            return stageMap.get(referenceObject);
        } else {
            stageMap.put(referenceObject,new Stage());
            return stageMap.get(referenceObject);
        }

    }

    /**
     * Remove a Stage from StageManager
     * @param referenceObject the corresponding reference object for the Stage
     * @return true if the map was modified as a result of this method call. False otherwise
     */
    public static boolean removeStage(PopWindow referenceObject) {

        if (stageMap.containsKey(referenceObject)) {
            stageMap.remove(referenceObject);
            return true;
        } else {
            return false;
        }

    }

    // Since PopWindows can be equivalent, get the stored PopWindow that is equivalent to a reference window
    private static PopWindow getStoredKey(PopWindow referenceWindow) {
        for (PopWindow window : stageMap.keySet()) {
            if (window.equals(referenceWindow)) {
                return window;
            }
        }
        return null;
    }

}
