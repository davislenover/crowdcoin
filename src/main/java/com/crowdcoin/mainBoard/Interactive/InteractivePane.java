package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class InteractivePane {

    // The idea is to have some object which can be passed into a Tab that defines how a tab interacts with the right display (intractable display)

    private GridPane parentFieldGridPane;
    private GridPane parentButtonGridPane;

    private List<TextFieldCombo> fieldsList;
    private List<ButtonCombo> buttonList;

    /**
     * Creates an InteractivePane object. InteractivePane's define how a Tab interacts with the rightmost display beside the TableView (by convention)
     * @param parentFieldGridPane the GridPane to house user intractable Fields. By convention, this Pane is above the Button GridPane
     * @param parentButtonGridPane the GridPane to house user intractable Buttons. By convention, this Pane is below the Fields GridPane
     */
    public InteractivePane(GridPane parentFieldGridPane, GridPane parentButtonGridPane) {
        this.parentFieldGridPane = parentFieldGridPane;
        this.parentButtonGridPane = parentButtonGridPane;
        this.fieldsList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
    }

    /**
     * Add a field to the GridPane. Creates a new row at the bottom of the Field GridPane and inserts the field there. All other rows are automatically resized such that they are all spaced out evenly
     * @param header the top text to appear with the TextField
     * @param description the text below the header. Typically used to convey what the TextField is used for
     * @return true if a new field was added, false otherwise
     * @Note by convention, Fields are added above the Button Grid vertically
     */
    public boolean addField(String header, String description) {

        // Create a new TextFieldCombo object containing the corresponding header and description
        TextFieldCombo newField = new TextFieldCombo(header,description);
        // Attempt to store in list
        boolean returnBool = this.fieldsList.add(newField);

        // If object fails to be added, no need to update the GridPane thus return here
        if (!returnBool) {
            return false;
        }

        // Given a new item was added, update row constraints to add another row
        updateParentDisplayPane();
        // Add field to pane
        newField.applyPane(this.parentFieldGridPane,this.fieldsList.size()-1);

        return returnBool;
    }

    // Method to update row constraints
    private void updateParentDisplayPane() {

        // Clear all prior constraints
        this.parentFieldGridPane.getRowConstraints().clear();

        // This method is usually called when a new field is added thus we need to update how much space each row will need (in height)
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.percentHeightProperty().setValue(100/this.fieldsList.size());

        // Apply updated property for however many rows are needed
        for (int i = 0; i < this.fieldsList.size(); i++) {
            this.parentFieldGridPane.getRowConstraints().add(rowConstraint);
        }

    }

    /**
     * Add a button to the GridPane. Creates a new column at the rightmost side of the Button GridPane and inserts the field there. All other columns are automatically resized such that they are all spaced out evenly
     * @param buttonText the text to be displayed by the Button
     * @param eventHandler the class containing an invokable method by the Button to perform arbitrary logic upon firing of an ActionEvent by the Button. Intended to allow users to execute arbitrary logic for each button and not singular unified logic
     * @return true if a new field was added, false otherwise
     * @Note by convention, Buttons are added horizontally below the Field Grid
     */
    public boolean addButton(String buttonText, ButtonHandler eventHandler) {

        ButtonCombo newButton = new ButtonCombo(buttonText,eventHandler,this);
        boolean returnBool = this.buttonList.add(newButton);

        if (!returnBool) {
            return false;
        }

        updateParentButtonPane();
        newButton.applyPane(this.parentButtonGridPane,this.buttonList.size()-1);

        return returnBool;

    }

    // Method to update column constraints for button grid
    // Unlike the field grid, buttons are added horizontally rather than vertically, thus column constraints is updated instead
    private void updateParentButtonPane() {

        this.parentButtonGridPane.getColumnConstraints().clear();

        ColumnConstraints columnConstraint = new ColumnConstraints();
        columnConstraint.percentWidthProperty().setValue(100/this.buttonList.size());

        // Apply updated property for however many columns are needed
        for (int i = 0; i < this.buttonList.size(); i++) {
            this.parentButtonGridPane.getColumnConstraints().add(columnConstraint);
        }

    }


    /**
     * Gets all user input from all TextFields
     * @return a list of strings corresponding to each TextField (top to bottom in the GirdPane)
     */
    public List<String> getAllInput() {

        List<String> returnList = new ArrayList<>();

        for (TextFieldCombo field : this.fieldsList) {

            returnList.add(field.getInput());

        }

        return returnList;

    }


}
