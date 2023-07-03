package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextArea;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.*;

public class InteractivePane implements Iterable<InputField> {

    // The idea is to have some object which can be passed into a GUI that defines how said GUI interacts with the user
    private List<InputField> fieldsList;
    private List<OutputField> outputFieldList;
    private List<InteractiveButton> buttonList;

    /**
     * Creates an InteractivePane object. InteractivePane's define how a GUI interacts with a user (by convention). This is intended as a parent class (framework) for child (specific) classes
     */
    public InteractivePane() {
        this.fieldsList = new ArrayList<>();
        this.buttonList = new ArrayList<>();
        this.outputFieldList = new ArrayList<>();
    }

    /**
     * Gets the size (count) of all InputFields within the InteractivePane
     * @return the size as an integer
     */
    public int getFieldsSize() {
        return this.fieldsList.size();
    }

    /**
     * Add an InputField to the InteractivePane. Sets the parent pane of the InputField to this instance of InteractivePane
     * @param newField the InputField object to add
     * @return true if the InputField object was added, false otherwise
     */
    public boolean addInputField(InputField newField) {

        if (this.fieldsList.add(newField)) {
            newField.setInteractivePane(this);
            return true;
        }
        return false;
    }

    /**
     * Add an OutputField to the InteractivePane. Sets the parent pane of the OutputField to this instance of InteractivePane
     * @param newField the OutputField object to add
     * @return true if the OutputField object was added, false otherwise
     */
    public boolean addOutputField(OutputField newField) {

        if (this.outputFieldList.add(newField)) {
            newField.setInteractivePane(this);
            return true;
        }
        return false;

    }

    /**
     * Add InputFields to the InteractivePane
     * @param newFields the InputField objects to add
     * @return true if the list was changed as a result of InputField objects being added, false otherwise
     */
    public boolean addAllInputFields(Collection<InputField> newFields) {

        if (this.fieldsList.addAll(newFields)) {
            for (InputField field : newFields) {
                field.setInteractivePane(this);
            }
            return true;
        }
        return false;

    }

    /**
     * Removes a specified field from the InteractivePane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param fieldIndex the index within the InteractivePane of the field to remove
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public void removeField(int fieldIndex) throws IndexOutOfBoundsException {

        this.fieldsList.remove(fieldIndex);

    }

    /**
     * Retains all fields within the given InputField collection, discards the rest
     * @param fieldsToRetain the InputField collection to retain
     */
    public void retainAllFields(Collection<InputField> fieldsToRetain) {
        if (this.fieldsList.retainAll(fieldsToRetain)) {
            for(InputField field : fieldsToRetain) {
                field.setInteractivePane(this);
            }
        }
    }

    /**
     * Gets a InputField object stored at the specified index position
     * @param fieldIndex the index position as an integer
     * @return an InputField object at the specified index position
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public InputField getInputField(int fieldIndex) {

        if (fieldIndex > this.fieldsList.size() - 1) {
            throw new IndexOutOfBoundsException("Index of " + fieldIndex + " is out of range for size " + this.fieldsList.size());
        }

        return this.fieldsList.get(fieldIndex);

    }

    /**
     * Gets a OutputField object stored at the specified index position
     * @param fieldIndex the index position as an integer
     * @return an OutputField object at the specified index position
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public OutputField getOutputField(int fieldIndex) {

        if (fieldIndex > this.outputFieldList.size() - 1) {
            throw new IndexOutOfBoundsException("Index of " + fieldIndex + " is out of range for size " + this.outputFieldList.size());
        }

        return this.outputFieldList.get(fieldIndex);

    }

    // Method to update row constraints
    // Called to setup spacing of fields evenly in GridPane
    private void updateParentDisplayPane(GridPane parentFieldGridPane) {

        // Clear all prior constraints
        parentFieldGridPane.getRowConstraints().clear();

        // Get combined input and output field list
        List<Field> combinedList = this.getCombinedList();

        // Update how much space each row will need (in height)
        RowConstraints rowConstraint = new RowConstraints();
        rowConstraint.percentHeightProperty().setValue(100/combinedList.size());

        // Apply updated property for however many rows are needed
        for (int i = 0; i < combinedList.size(); i++) {
            parentFieldGridPane.getRowConstraints().add(rowConstraint);
        }

    }

    /**
     * Add a button to InteractivePane. When applyInteractivePane() is called, all buttons added will be applied to the corresponding button GridPane
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
     * Add an already created InteractiveButton to the InteractivePane. Also used by child classes to add buttons to parent class
     * @param newButton the button to add as an InteractiveButton object
     * @return
     */
    public boolean addButton(InteractiveButton newButton) {
        return this.buttonList.add(newButton);
    }

    /**
     * Removes a specified button from the InteractivePane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param buttonIndex the index within the InteractivePane of the button to remove
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

        // Get combined input and output field list
        List<Field> combinedFieldList = this.getCombinedList();

        // Apply fields
        for (int fieldIndex = 0; fieldIndex < combinedFieldList.size(); fieldIndex++) {
            // Get field
            Field currentField = combinedFieldList.get(fieldIndex);
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
     * Gets the iterator for all InputFields
     * @return an Iterator object
     */
    @Override
    public Iterator<InputField> iterator() {
        return this.fieldsList.iterator();
    }

    // Take both input and output fields, add them all to a list and sort based on order
    private List<Field> getCombinedList() {

        List<Field> combinedList = new ArrayList<>() {{
            addAll(fieldsList);
            addAll(outputFieldList);
        }};

        // Sort list according to order integers
        combinedList.sort(Comparator.comparingInt(Field::getOrder));

        return combinedList;

    }
}
