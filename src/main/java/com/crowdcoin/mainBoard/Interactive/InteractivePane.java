package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class InteractivePane {

    // The idea is to have some object which can be passed into a Tab that defines how a tab interacts with the right display (intractable display)

    private GridPane parentGridPane;

    private List<TextFieldCombo> fieldsList;

    public InteractivePane(GridPane parentGridPane) {
        this.parentGridPane = parentGridPane;
        this.fieldsList = new ArrayList<>();
    }

    /**
     * Add a field to the GridPane. Creates a new row at the bottom of the GridPane and inserts the field there. All other rows are automatically resized such that they are all spaced out evenly
     * @param header the top text to appear with the TextField
     * @param description the text below the header. Typically used to convey what the TextField is used for
     * @return true if a new field was added, false otherwise
     */
    public boolean addField(String header, String description) {

        // Create a new TextFieldCombo object containing the corresponding header and description
        TextFieldCombo newField = new TextFieldCombo(header,description);
        // Attempt to store in list
        boolean returnbool = this.fieldsList.add(newField);

        // If object fails to be added, no need to update the GridPane thus return here
        if (!returnbool) {
            return false;
        }

        // Given a new item was added, update row constraints to add another row
        updateParentPane();
        // Add field to pane
        newField.applyPane(this.parentGridPane,this.fieldsList.size()-1);

        return returnbool;
    }

    // Method to update row constraints
    private void updateParentPane() {

        // Clear all prior constraints
        this.parentGridPane.getRowConstraints().clear();

        // This method is usually called when a new field is added thus we need to update how much space each row will need (in height)
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.percentHeightProperty().setValue(100/this.fieldsList.size());

        // Apply updated property for however many rows are needed
        for (int i = 0; i < this.fieldsList.size(); i++) {
            this.parentGridPane.getRowConstraints().add(rowConstraint);
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
