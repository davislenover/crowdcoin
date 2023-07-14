package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.util.*;

public class InteractivePane implements Iterable<InputField>, Observable<ModifyEvent> {

    // The idea is to have some object which can be passed into a GUI that defines how said GUI interacts with the user
    private List<InputField> inputFieldsList;
    private List<OutputField> outputFieldList;
    private List<SubmitField> submitFieldList;

    private List<Observer<ModifyEvent>> subscriptionList;

    /**
     * Creates an InteractivePane object. InteractivePane's define how a GUI interacts with a user (by convention). This is intended as a parent class (framework) for child (specific) classes
     */
    public InteractivePane() {
        this.inputFieldsList = new ArrayList<>();
        this.submitFieldList = new ArrayList<>();
        this.outputFieldList = new ArrayList<>();
        this.subscriptionList = new ArrayList<>();
    }

    /**
     * Gets the size (count) of all InputFields within the InteractivePane
     * @return the size as an integer
     */
    public int getFieldsSize() {
        return this.inputFieldsList.size();
    }

    /**
     * Gets the size (count) of all SubmitFields within the InteractivePane
     * @return the size as an integer
     */
    public int getSubmitFieldsSize() {
        return this.submitFieldList.size();
    }

    /**
     * Add an InputField to the InteractivePane. Sets the parent pane of the InputField to this instance of InteractivePane. By convention, InputFields are added vertically in a GridPane (i.e., one row contains one InputField). The object's order dictates the order in which object's a added to the GridPane (top to bottom)
     * @param newField the InputField object to add
     * @return true if the InputField object was added, false otherwise
     */
    public boolean addInputField(InputField newField) {

        if (this.inputFieldsList.add(newField)) {
            newField.setInteractivePane(this);
            return true;
        }
        return false;
    }

    /**
     * Add an OutputField to the InteractivePane. Sets the parent pane of the OutputField to this instance of InteractivePane. By convention, OutputFields are added vertically in a GridPane (i.e., one row contains one OutputField). The object's order dictates the order in which object's a added to the GridPane (top to bottom)
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

        if (this.inputFieldsList.addAll(newFields)) {
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

        this.inputFieldsList.remove(fieldIndex);

    }

    /**
     * Retains all fields within the given InputField collection, discards the rest
     * @param fieldsToRetain the InputField collection to retain
     */
    public void retainAllInputFields(Collection<InputField> fieldsToRetain) {
        if (this.inputFieldsList.retainAll(fieldsToRetain)) {
            for(InputField field : fieldsToRetain) {
                field.setInteractivePane(this);
            }
        }
    }

    /**
     * Clear all fields from the InputField collection. This does not reset the attached InteractivePane to every InputField. To change the InteractivePane attached to any InputField, add to another pane
     */
    public void clearAllInputFields() {
        this.inputFieldsList.clear();
    }

    /**
     * Clear all fields from the SubmitField collection. This does not reset the attached InteractivePane to every SubmitField. To change the InteractivePane attached to any SubmitField, add to another pane
     */
    public void clearAllSubmitFields() {
        this.submitFieldList.clear();
    }

    /**
     * Clear all fields from the OutputField collection. This does not reset the attached InteractivePane to every OutputField. To change the InteractivePane attached to any OutputField, add to another pane
     */
    public void clearAllOutputFields() {
        this.outputFieldList.clear();
    }

    /**
     * Gets a InputField object stored at the specified index position
     * @param fieldIndex the index position as an integer
     * @return an InputField object at the specified index position
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public InputField getInputField(int fieldIndex) {

        if (fieldIndex > this.inputFieldsList.size() - 1) {
            throw new IndexOutOfBoundsException("Index of " + fieldIndex + " is out of range for size " + this.inputFieldsList.size());
        }

        return this.inputFieldsList.get(fieldIndex);

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

        if (!combinedList.isEmpty()) {
            // Update how much space each row will need (in height)
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.percentHeightProperty().setValue(100/combinedList.size());

            // Apply updated property for however many rows are needed
            for (int i = 0; i < combinedList.size(); i++) {
                parentFieldGridPane.getRowConstraints().add(rowConstraint);
            }
        }

    }

    /**
     * Add an SubmitField to the InteractivePane. Also used by child classes to add SubmitFields to parent class. Sets the parent pane of the SubmitField to this instance of InteractivePane. By convention, SubmitFields are added in a row at the bottom of the window in a GridPane (each column holds a SubmitField). The order at which they are added is determined by each object's order property.
     * It is imperative that each button within this list has a successive order (i.e., no SubmitField skips an order number)
     * @param newField the field to add as an SubmitField object
     * @return true if the collection was modified as a result of invocation of this method, false otherwise.
     */
    public boolean addSubmitField(SubmitField newField) {
        if (this.submitFieldList.add(newField)) {
            newField.setInteractivePane(this);
            return true;
        }
        return false;
    }

    /**
     * Removes a specified SubmitField from the InteractivePane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param fieldIndex the index within the InteractivePane of the Field to remove
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    public void removeSubmitField(int fieldIndex) throws IndexOutOfBoundsException {

        this.submitFieldList.remove(fieldIndex);

    }

    // Method to update column constraints for SubmitField grid
    // Unlike the field grid, buttons are added horizontally rather than vertically, thus column constraints is updated instead
    private void updateParentSubmitFieldPane(GridPane parentSubmitFieldGridPane) {

        parentSubmitFieldGridPane.getColumnConstraints().clear();

        if (!this.submitFieldList.isEmpty()) {
            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.percentWidthProperty().setValue(100/this.submitFieldList.size());

            // Apply updated property for however many columns are needed
            for (int i = 0; i < this.submitFieldList.size(); i++) {
                parentSubmitFieldGridPane.getColumnConstraints().add(columnConstraint);
            }
        }

    }

    /**
     * Applys Fields and Buttons to GridPanes.
     * @param parentFieldGridPane the target GridPane to insert fields into. Upon application, for each field, creates a new row at the bottom of the Field GridPane and inserts a new field there. All other rows are automatically resized such that they are all spaced out evenly.
     * @param parentSubmitFieldPane the target GridPane to insert SubmitField into. Upon application, for each SubmitField, Creates a new column at the rightmost side of the SubmitField GridPane and inserts the field there. All other columns are automatically resized such that they are all spaced out evenly.
     * @Note by convention, the field GridPane should be above the SubmitField GridPane
     */
    public void applyInteractivePane(GridPane parentFieldGridPane, GridPane parentSubmitFieldPane) {

        // Clear both GridPanes
        parentFieldGridPane.getChildren().clear();
        parentSubmitFieldPane.getChildren().clear();

        // Clear and update column/row constraints for fitting instances of TextFields and Buttons
        updateParentDisplayPane(parentFieldGridPane);
        updateParentSubmitFieldPane(parentSubmitFieldPane);

        // Get combined input and output field list
        List<Field> combinedFieldList = this.getCombinedList();

        // Apply fields
        for (int fieldIndex = 0; fieldIndex < combinedFieldList.size(); fieldIndex++) {
            // Get field
            Field currentField = combinedFieldList.get(fieldIndex);
            // Apply to GirdPane in corresponding index location (same as fieldsList index)
            currentField.applyPane(parentFieldGridPane,fieldIndex);
        }

        // Apply SubmitFields
        for (int buttonIndex = 0; buttonIndex < this.submitFieldList.size(); buttonIndex++) {
            // Get Button
            SubmitField currentButton = this.submitFieldList.get(buttonIndex);
            // Apply to GirdPane in corresponding index location (same as buttonList index)
            currentButton.applyPane(parentSubmitFieldPane,0);
        }


    }

    /**
     * Gets all user input from all InputFields
     * @return a list of strings corresponding to each InputField (top to bottom in the GirdPane)
     */
    public List<String> getAllInput() {

        List<String> returnList = new ArrayList<>();

        for (InputField field : this.inputFieldsList) {

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
        return this.inputFieldsList.iterator();
    }

    // Take both input and output fields, add them all to a list and sort based on order
    private List<Field> getCombinedList() {

        List<Field> combinedList = new ArrayList<>() {{
            addAll(inputFieldsList);
            addAll(outputFieldList);
        }};

        // Sort list according to order integers
        combinedList.sort(Comparator.comparingInt(Field::getOrder));

        return combinedList;

    }

    @Override
    public boolean addObserver(Observer<ModifyEvent> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ModifyEvent> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(ModifyEvent event) {

        for (Observer<ModifyEvent> observer : this.subscriptionList) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }
}
