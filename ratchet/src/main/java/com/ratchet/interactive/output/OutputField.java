package com.ratchet.interactive.output;

import com.ratchet.interactive.Field;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public interface OutputField extends Field {

    /**
     * Method invoked to apply the given OutputField to a GridPane. The location within the GridPane where the OutputField is added should be handled by the GridPane, however, it may be customary to utilize a container defined within the OutputField class
     * to format the OutputField (container holds the JavaFX field object). In such a case, the container is applied to the GridPane
     * @param targetPane the GridPane object to add the OutputField to
     * @param targetRow the target row of the GridPane object to add the OutputField to
     */
    void applyPane(GridPane targetPane, int targetRow);

    /**
     * Gets the corresponding JavaFX pane from the given OutputField
     * @return a OutputFields corresponding JavaFX Pane object
     */
    Pane getPane();

    /**
     * Sets the value of the OutputField
     * @param value the value to display as a String
     */
    void setValue(String value);

    /**
     * Sets the wrapping width of the main text within the OutputField
     * @param width the given width as a double
     */
    void setValueWrappingWidth(double width);

}
