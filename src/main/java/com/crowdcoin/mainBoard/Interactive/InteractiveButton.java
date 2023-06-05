package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class InteractiveButton {

    private Button button;
    private InteractiveButtonActionEvent interactiveButtonActionEvent;
    private InteractiveTabPane parentPane;

    /**
     * Houses a button object on a column of a GridPane. Sets button to invoke corresponding method in InteractiveButtonActionEvent class upon interaction event is fired from Button object
     * @param buttonText the text displayed by the button
     * @param buttonActionHandler the class which contains an invokable method by the Button (as specified by the InteractiveButtonActionEvent interface). Intended to allow for a variety of different logic to be executed for any given Button
     * @param pane the InteractiveTabPane object to house the InteractiveButton object (i.e., the parent)
     * @Note this is the lower level object used in InteractiveTabPane's
     */
    public InteractiveButton(String buttonText, InteractiveButtonActionEvent buttonActionHandler, InteractiveTabPane pane) {

        // Create a new button with specified text
        this.button = new Button(buttonText);
        // Buttons will fill the size of parent (e.g., button will fill a cell in a GridPane)
        // Arranging the X and Y translations of the button in the cell specifically do not matter as the button is filling the entire cell
        this.button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);

        this.interactiveButtonActionEvent = buttonActionHandler;
        this.parentPane = pane;

        // Set the button to invoke the given handleButtonClick() method found within the corresponding interactiveButtonActionEvent class when interacted with
        this.button.setOnAction(event -> this.interactiveButtonActionEvent.buttonActionHandler(event,this.button,this.parentPane));

    }

    /**
     * Add object to a GridPane
     * @param targetPane the GridPane object to add the object to
     * @param targetColumn the target column of the GridPane object to add the object to. Note that Button's are added horizontally and NOT vertically like InteractiveTextField's
     */
    public void applyPane(GridPane targetPane, int targetColumn, int targetRow) {
        targetPane.add(this.button,targetColumn,targetRow);
    }


}
