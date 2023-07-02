package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public interface Field {

    /**
     * Method invoked to apply the given Field to a GridPane. The location within the GridPane where the Field is added should be handled by the GridPane, however, it may be customary to utilize a container defined within the Field class
     * to format the Field (container holds the JavaFX field object). In such a case, the container is applied to the GridPane
     * @param targetPane the GridPane object to add the Field to
     * @param targetRow the target row of the GridPane object to add the Field to
     */
    void applyPane(GridPane targetPane, int targetRow);

    /**
     * Gets the corresponding JavaFX pane from the given Field
     * @return a Fields corresponding JavaFX Pane object
     */
    Pane getPane();

    /**
     * Sets the InteractiveInputPane the Field belongs to. This pane will be passed on InteractiveFieldActionEvent trigger
     * @param pane the corresponding InteractiveInputPane object
     */
    void setInteractivePane(InteractiveInputPane pane);

}
