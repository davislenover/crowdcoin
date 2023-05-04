package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

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


}
