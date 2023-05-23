package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.layout.GridPane;

public interface InputField {

    /**
     * Method invoked to apply the given InputField to a GridPane. The location within the GridPane where the InputField is added should be handled by the GridPane, however, it may be customary to utilize a container defined within the InputField class
     * to format the InputField (container holds the JavaFX field object). In such a case, the container is applied to the GridPane
     * @param targetPane the GridPane object to add the InputField to
     * @param targetRow the target row of the GridPane object to add the InputField to
     */
    void applyPane(GridPane targetPane, int targetRow);

    /**
     * Method invoked to get the current user input text (or selection) within the InputField object
     * @return the text (or selection) present as a String
     */
    String getInput();

}
