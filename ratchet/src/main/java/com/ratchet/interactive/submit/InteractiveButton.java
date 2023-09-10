package com.ratchet.interactive.submit;

import com.ratchet.interactive.InteractiveButtonActionEvent;
import com.ratchet.interactive.InteractivePane;
import com.ratchet.interactive.submit.SubmitField;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class InteractiveButton implements SubmitField {

    private Button button;
    private StackPane containerPane;
    private InteractiveButtonActionEvent interactiveButtonActionEvent;
    private InteractivePane parentPane;

    private int order;

    /**
     * Houses a button object on a column of a GridPane. Sets button to invoke corresponding method in InteractiveButtonActionEvent class upon interaction event is fired from Button object
     * @param buttonText the text displayed by the button
     * @param buttonActionHandler the class which contains an invokable method by the Button (as specified by the InteractiveButtonActionEvent interface). Intended to allow for a variety of different logic to be executed for any given Button
     * @Note this is the lower level object used in InteractivePane's
     */
    public InteractiveButton(String buttonText, InteractiveButtonActionEvent buttonActionHandler) {

        // Create a new button with specified text
        this.button = new Button(buttonText);
        // Buttons will fill the size of parent (e.g., button will fill a cell in a GridPane)
        // Arranging the X and Y translations of the button in the cell specifically do not matter as the button is filling the entire cell
        this.button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);

        this.interactiveButtonActionEvent = buttonActionHandler;

        // Set the button to invoke the given handleButtonClick() method found within the corresponding interactiveButtonActionEvent class when interacted with
        this.button.setOnAction(event -> this.interactiveButtonActionEvent.buttonActionHandler(event,this.button,this.parentPane));

        // Add button to container
        this.containerPane = new StackPane();
        this.containerPane.getChildren().add(this.button);

        // Set order
        this.order = 0;

    }

    /**
     * Add object to a GridPane
     * @param targetPane the GridPane object to add the object to
     * @param targetColumn the target column of the GridPane object to add the object to. Note that Button's are added horizontally and NOT vertically like InteractiveTextField's
     */
    public void applyPane(GridPane targetPane, int targetColumn, int targetRow) {
        targetPane.add(this.button,targetColumn,targetRow);
    }


    @Override
    public void applyPane(GridPane targetPane, int targetRow) {
        targetPane.add(this.button,this.order,targetRow);
    }

    @Override
    public void disable() {
        this.button.setDisable(true);
    }

    @Override
    public void enable() {
        this.button.setDisable(false);
    }

    @Override
    public Pane getPane() {
        return this.containerPane;
    }

    @Override
    public void setInteractivePane(InteractivePane pane) {
        this.parentPane = pane;
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
