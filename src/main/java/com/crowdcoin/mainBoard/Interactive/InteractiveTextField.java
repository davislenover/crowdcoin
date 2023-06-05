package com.crowdcoin.mainBoard.Interactive;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class InteractiveTextField implements InputField {

    // Defaults

    // TextField (user input)
    private static int textFieldWidth = 200;
    private static int textFieldTranslateX = 200;

    // TextField header
    private static int fieldHeaderTranslateX = 20;
    private static int fieldHeaderTranslateY = -30;
    private static int fieldHeaderWrappingWidth = 100;

    // TextField description
    private static int fieldDescTranslateX = 20;
    private static int fieldDescWrappingWidth = 200;

    private StackPane containerPane;
    private TextField textField;
    private Text fieldHeader;
    private Text fieldDescription;

    /**
     * Houses three node objects which are used in a single row on a GridPane
     * @param header the header for the column
     * @param description the description of what the text field (user input) is used for
     * @Note this is the lower level object used in InteractiveTabPane's
     */
    public InteractiveTextField(String header, String description) {

        // All these objects are housed in a parent pane, a StackPane for organization
        this.textField = new TextField();
        this.fieldHeader = new Text(header);
        this.fieldDescription = new Text(description);

        this.containerPane = new StackPane();
        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        // By default, items are added to the center of the StackPane and the node (as in the JavaFX object like TextField or Text) will be stretched to fit the entire available space
        this.containerPane.getChildren().addAll(this.textField,this.fieldHeader,this.fieldDescription);

        // Set max width relative to position in StackPlane (currently center)
        this.textField.setMaxWidth(textFieldWidth);
        // Shift the TextField by X pixels to the right from original position in StackPlane
        this.textField.setTranslateX(textFieldTranslateX);

        // Likewise for header
        // First set its relative position from default (center) to center left
        this.containerPane.setAlignment(this.fieldHeader,Pos.CENTER_LEFT);
        // Negative y is up
        this.fieldHeader.setTranslateY(fieldHeaderTranslateY);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX);
        // Wrapping width defines how long a single line of text can be before moving to a new line
        this.fieldHeader.setWrappingWidth(fieldHeaderWrappingWidth);

        this.containerPane.setAlignment(this.fieldDescription,Pos.CENTER_LEFT);
        this.fieldDescription.setTranslateX(fieldDescTranslateX);
        this.fieldDescription.setWrappingWidth(fieldDescWrappingWidth);

    }

    /**
     * Add object to a GridPane
     * @param targetPane the GridPane object to add the object to
     * @param targetRow the target row of the GridPane object to add the object to
     */
    public void applyPane(GridPane targetPane, int targetRow) {
        targetPane.add(this.containerPane,0,targetRow);
    }

    public Pane getPane() {
        return this.containerPane;
    }

    /**
     * Gets the current text present within the TextField
     * @return the text present as a String
     */
    public String getInput() {
        return this.textField.getText();
    }

}
