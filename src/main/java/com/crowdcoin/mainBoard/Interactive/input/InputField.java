package com.crowdcoin.mainBoard.Interactive.input;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.mainBoard.Interactive.Field;
import com.crowdcoin.mainBoard.Interactive.InteractiveInputPane;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public interface InputField extends Field {

    /**
     * Method invoked to apply the given InputField to a GridPane. The location within the GridPane where the InputField is added should be handled by the GridPane, however, it may be customary to utilize a container defined within the InputField class
     * to format the InputField (container holds the JavaFX field object). In such a case, the container is applied to the GridPane
     * @param targetPane the GridPane object to add the InputField to
     * @param targetRow the target row of the GridPane object to add the InputField to
     */
    void applyPane(GridPane targetPane, int targetRow);

    /**
     * Gets the corresponding JavaFX pane from the given InputField
     * @return a InputFields corresponding JavaFX Pane object
     */
    Pane getPane();

    /**
     * Method invoked to get the current user input text (or selection) within the InputField object
     * @return the text (or selection) present as a String
     */
    String getInput();

    /**
     * Gets the InfoBox object used by the InputField. An info box appears underneath the InputField, used to provide helpful additional messaging
     */
    InfoBox getInfoBox();

    /**
     * Display the info box. By default, the info box is displayed below the InputField. To configure its message, use setInfo()
     */
    void showInfo();

    /**
     * Hide the info box.
     */
    void hideInfo();

    /**
     * Pre-Set a specific value in the InputField. Change will immediately apply on-screen if object was set to screen by a InteractiveInputPane
     * @param value the given String to display
     */
    void setValue(String value);

    /**
     * Sets the spacing between all text and the field. Text and field are translated from original creation position (i.e., original position +- spacing)
     * @param spacing the spacing as an integer
     */
    void setSpacing(int spacing);

    /**
     * Sets the maximum with of a single line of text before moving to a new line
     * @param wrappingWidth the width as an integer
     */
    void setDescWrappingWidth(int wrappingWidth);

    /**
     * Add a new input validator to the InputField
     * @param validator the given validator as an InputValidator object
     */
    void addValidator(InputValidator validator);

    /**
     * Remove a validator at a given index
     * @param index the index to request to remove. May throw IndexOutOfRangeException if improper index is passed
     */
    void removeValidator(int index);

    /**
     * Validates the current fields' input
     * @return true if field input is validated
     * @throws ValidationException if field input could not be validated, exception message will state where input failed
     */
    boolean validateField() throws ValidationException;


    /**
     * Sets the InteractiveInputPane the field belongs to. This pane will be passed on InteractiveFieldActionEvent trigger
     * @param pane the corresponding InteractiveInputPane object
     */
    void setInteractivePane(InteractiveInputPane pane);

}
