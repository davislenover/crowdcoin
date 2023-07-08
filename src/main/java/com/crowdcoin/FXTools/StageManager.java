package com.crowdcoin.FXTools;

import javafx.stage.Stage;

import java.util.HashMap;

/**
 * Acts as a container for JavaFX stages
 */
public class StageManager {

    private static HashMap<Object,Stage> stageMap = new HashMap<>();

    /**
     * Gets the corresponding JavaFX Stage for an Object. If the Object does not have a corresponding Stage, a new one is created, stored and returned
     * @param referenceObject the given Object to get a Stage for
     * @return a JavaFX Stage object
     */
    public static Stage getStage(Object referenceObject) {

        if (stageMap.containsKey(referenceObject)) {
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
    public static boolean removeStage(Object referenceObject) {

        if (stageMap.containsKey(referenceObject)) {
            stageMap.remove(referenceObject);
            return true;
        } else {
            return false;
        }

    }

}
