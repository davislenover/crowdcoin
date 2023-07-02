package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.mainBoard.Interactive.input.InputField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface InteractivePane<T extends Field> extends Iterable<T> {


    /**
     * Gets the size (count) of all Fields within the InteractivePane (not including buttons)
     * @return the size as an integer
     */
    int getFieldsSize();

    /**
     * Add an Field to the InteractivePane. Sets the parent pane of the Field to this instance of InteractivePane
     * @param newField the Field object to add
     * @return true if the Field object was added, false otherwise
     */
    boolean addField(T newField);


    /**
     * Add already created Fields to the InteractivePane
     *
     * @param newFields the Field objects to add
     * @return true if the list was changed as a result of Field objects being added, false otherwise
     */
    boolean addAllFields(Collection<T> newFields);

    /**
     * Removes a specified field from the InteractivePane. Note removal change does NOT take effect on GridPane until application method is invoked
     * @param fieldIndex the index within the InteractivePane of the field to remove
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    void removeField(int fieldIndex) throws IndexOutOfBoundsException;

    /**
     * Retains all fields within the given Field collection, discards the rest
     * @param fieldsToRetain the Field collection to retain
     */
    void retainAllFields(Collection<T> fieldsToRetain);

    /**
     * Gets a Field object stored at the specified index position
     * @param fieldIndex the index position as an integer
     * @return an Field object at the specified index position
     * @throws IndexOutOfBoundsException if fieldIndex is not within the range of the list
     */
    T getField(int fieldIndex);

    /**
     * Add a button to InteractivePane. When applyInteractivePane() is called, all buttons added will be applied to the corresponding button GridPane
     * @param buttonText the text to be displayed by the Button
     * @param eventHandler the class containing an invokable method by the Button to perform arbitrary logic upon firing of an ActionEvent by the Button. Intended to allow users to execute arbitrary logic for each button and not singular unified logic
     * @return true if a new field was added, false otherwise
     * @Note by convention, Buttons are added horizontally below the Field Grid horizontally
     */
    boolean addButton(String buttonText, InteractiveButtonActionEvent eventHandler);

    /**
     * Add an already created InteractiveButton to the InteractivePane. Also used by child classes to add buttons to parent class
     * @param newButton the button to add as an InteractiveButton object
     * @return
     */
    boolean addButton(InteractiveButton newButton);

    /**
     * Applys Fields and Buttons to GridPanes.
     * @param parentFieldGridPane the target GridPane to insert fields into. Upon application, for each field, creates a new row at the bottom of the Field GridPane and inserts a new field there. All other rows are automatically resized such that they are all spaced out evenly.
     * @param parentButtonGridPane the target GridPane to insert buttons into. Upon application, for each button, Creates a new column at the rightmost side of the Button GridPane and inserts the field there. All other columns are automatically resized such that they are all spaced out evenly.
     * @Note by convention, the field GridPane should be above the button GridPane
     */
    void applyInteractivePane(GridPane parentFieldGridPane, GridPane parentButtonGridPane);


}
