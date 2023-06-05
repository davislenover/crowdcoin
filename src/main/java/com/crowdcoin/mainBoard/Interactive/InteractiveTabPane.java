package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.ArrayList;
import java.util.List;

public class InteractiveTabPane {

    // The idea is to have some object which can be passed into a Tab that defines how a tab interacts with the right display (intractable display)
    private List<InputField> fieldsList;
    private List<InteractiveButton> buttonList;
    private SQLTable sqlTable;

    /**
     * Creates an InteractiveTabPane object. InteractiveTabPane's define how a Tab interacts with the rightmost display beside the TableView (by convention).
     * This object is typically used in tandem with a Tab object (as a Tab handles invocation of applying InteractiveTabPane to GridPanes)
     * @param table the SQLTable object which communicates with the SQL database, typically defined by the Tab
     */
    public InteractiveTabPane(SQLTable table) {
        this.fieldsList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.sqlTable = table;
    }

    /**
     * Add a field to the GridPane. When applyInteractivePane() is called, all fields added will be applied to the corresponding field GridPane
     * @param header the top text to appear with the TextField
     * @param description the text below the header. Typically used to convey what the TextField is used for
     * @return true if a new field was added, false otherwise
     * @Note by convention, Fields are added above the Button Grid vertically
     */
    public boolean addField(String header, String description) {

        // Create a new InteractiveTextField object containing the corresponding header and description
        InputField newField = new InteractiveTextField(header,description);
        // Attempt to store in list
        return this.fieldsList.add(newField);
    }

    /**
     * Removes a specified field from the InteractiveTabPane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param fieldIndex the index within the InteractiveTabPane of the field to remove
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public void removeField(int fieldIndex) throws IndexOutOfBoundsException {

        this.fieldsList.remove(fieldIndex);

    }

    /**
     * Add a field to the GridPane (Choice box). When applyInteractivePane() is called, all fields added will be applied to the corresponding field GridPane
     * @param header the top text to appear with the ChoiceBox
     * @param description the text below the header. Typically used to convey what the ChoiceBox is used for
     * @param options the options ChoiceBox will display in its dropdown for the user to select
     * @return true if a new field was added, false otherwise
     */
    public boolean addChoiceField(String header, String description, String ... options) {

        InteractiveChoiceBox newField = new InteractiveChoiceBox(header,description);
        // Add all options to ChoiceBox
        for (String option : options) {
            newField.addValue(option);
        }
        return this.fieldsList.add(newField);

    }

    // Method to update row constraints
    // Called to setup spacing of fields evenly in GridPane
    private void updateParentDisplayPane(GridPane parentFieldGridPane) {

        // Clear all prior constraints
        parentFieldGridPane.getRowConstraints().clear();

        // Update how much space each row will need (in height)
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.percentHeightProperty().setValue(100/this.fieldsList.size());

        // Apply updated property for however many rows are needed
        for (int i = 0; i < this.fieldsList.size(); i++) {
            parentFieldGridPane.getRowConstraints().add(rowConstraint);
        }

    }

    /**
     * Add a button to InteractiveTabPane. When applyInteractivePane() is called, all buttons added will be applied to the corresponding button GridPane
     * @param buttonText the text to be displayed by the Button
     * @param eventHandler the class containing an invokable method by the Button to perform arbitrary logic upon firing of an ActionEvent by the Button. Intended to allow users to execute arbitrary logic for each button and not singular unified logic
     * @return true if a new field was added, false otherwise
     * @Note by convention, Buttons are added horizontally below the Field Grid horizontally
     */
    public boolean addButton(String buttonText, InteractiveButtonActionEvent eventHandler) {
        InteractiveButton newButton = new InteractiveButton(buttonText,eventHandler,this);
        return this.buttonList.add(newButton);
    }

    /**
     * Removes a specified button from the InteractiveTabPane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param buttonIndex the index within the InteractiveTabPane of the button to remove
     * @throws IndexOutOfBoundsException if buttonIndex is not within the range of the list
     */
    public void removeButton(int buttonIndex) throws IndexOutOfBoundsException {

        this.buttonList.remove(buttonIndex);

    }

    // Method to update column constraints for button grid
    // Unlike the field grid, buttons are added horizontally rather than vertically, thus column constraints is updated instead
    private void updateParentButtonPane(GridPane parentButtonGridPane) {

        parentButtonGridPane.getColumnConstraints().clear();

        ColumnConstraints columnConstraint = new ColumnConstraints();
        columnConstraint.percentWidthProperty().setValue(100/this.buttonList.size());

        // Apply updated property for however many columns are needed
        for (int i = 0; i < this.buttonList.size(); i++) {
            parentButtonGridPane.getColumnConstraints().add(columnConstraint);
        }

    }

    /**
     * Applys Fields and Buttons to GridPanes.
     * @param parentFieldGridPane the target GridPane to insert fields into. Upon application, for each field, creates a new row at the bottom of the Field GridPane and inserts a new field there. All other rows are automatically resized such that they are all spaced out evenly.
     * @param parentButtonGridPane the target GridPane to insert buttons into. Upon application, for each button, Creates a new column at the rightmost side of the Button GridPane and inserts the field there. All other columns are automatically resized such that they are all spaced out evenly.
     * @Note by convention, the field GridPane should be above the button GridPane
     */
    public void applyInteractivePane(GridPane parentFieldGridPane, GridPane parentButtonGridPane) {

        // Clear both GridPanes
        parentFieldGridPane.getChildren().clear();
        parentButtonGridPane.getChildren().clear();

        // Clear and update column/row constraints for fitting instances of TextFields and Buttons
        updateParentDisplayPane(parentFieldGridPane);
        updateParentButtonPane(parentButtonGridPane);

        // Apply fields
        for (int fieldIndex = 0; fieldIndex < this.fieldsList.size(); fieldIndex++) {
            // Get field
            InputField currentField = this.fieldsList.get(fieldIndex);
            // Apply to GirdPane in corresponding index location (same as fieldsList index)
            currentField.applyPane(parentFieldGridPane,fieldIndex);
        }

        // Apply buttons
        for (int buttonIndex = 0; buttonIndex < this.buttonList.size(); buttonIndex++) {
            // Get Button
            InteractiveButton currentButton = this.buttonList.get(buttonIndex);
            // Apply to GirdPane in corresponding index location (same as buttonList index)
            currentButton.applyPane(parentButtonGridPane,buttonIndex,0);
        }


    }

    /**
     * Gets all user input from all TextFields
     * @return a list of strings corresponding to each TextField (top to bottom in the GirdPane)
     */
    public List<String> getAllInput() {

        List<String> returnList = new ArrayList<>();

        for (InputField field : this.fieldsList) {

            returnList.add(field.getInput());

        }

        return returnList;

    }

    /**
     * Gets the corresponding SQLTable object for communicating with the SQL database
     * @return SQLTable object
     */
    public SQLTable getSqlTable() {
        return this.sqlTable;
    }


}
