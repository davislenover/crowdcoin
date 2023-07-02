package com.crowdcoin.mainBoard.Interactive.input;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractiveInputPane;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.ValidatorManager;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.util.Collection;
import java.util.List;

public class InteractiveChoiceBox implements InputField {

    // Defaults

    // ChoiceBox (user input)
    private static int choiceBoxWidth = 200;
    private static int choiceBoxTranslateX = 200;

    // ChoiceBox header
    private static int fieldHeaderTranslateX = 20;
    private static int fieldHeaderTranslateY = -30;
    private static int fieldHeaderWrappingWidth = 100;

    // ChoiceBox description
    private static int fieldDescTranslateX = 20;
    private static int fieldDescWrappingWidth = 180;

    private StackPane containerPane;
    private ChoiceBox<String> choiceBox;
    private Text fieldHeader;
    private Text fieldDescription;

    // Event
    private InteractiveFieldActionEvent interactiveFieldActionEvent;
    private InteractiveInputPane parentPane;

    // Info
    private InfoBox infoBox;

    // Input validators
    private ValidatorManager validatorManager;

    /**
     * Houses three node objects which are used in a single row on a GridPane
     * @param header the header for the column
     * @param description the description of what the ChoiceBox field (user input) is used for
     * @Note this is the lower level object used in InteractiveTabInputPane's
     */
    public InteractiveChoiceBox(String header, String description, InteractiveFieldActionEvent actionEvent) {

        this.choiceBox = new ChoiceBox<>();
        this.fieldHeader = new Text(header);
        this.fieldDescription = new Text(description);

        this.containerPane = new StackPane();
        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        this.containerPane.getChildren().addAll(this.choiceBox,this.fieldHeader,this.fieldDescription);

        this.choiceBox.setMaxWidth(choiceBoxWidth);
        this.choiceBox.setTranslateX(choiceBoxTranslateX);

        this.containerPane.setAlignment(this.fieldHeader, Pos.CENTER_LEFT);
        this.fieldHeader.setTranslateY(fieldHeaderTranslateY);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX);
        this.fieldHeader.setWrappingWidth(fieldHeaderWrappingWidth);

        this.containerPane.setAlignment(this.fieldDescription,Pos.CENTER_LEFT);
        this.fieldDescription.setTranslateX(fieldDescTranslateX);
        this.fieldDescription.setWrappingWidth(fieldDescWrappingWidth);

        this.interactiveFieldActionEvent = actionEvent;
        this.choiceBox.setOnAction(event -> this.interactiveFieldActionEvent.fieldActionHandler(event,this.choiceBox,this.parentPane));

        // Set default info box
        this.infoBox = new InfoBox("Default message");

        // Create new input validator manager
        this.validatorManager = new ValidatorManager();

    }

    /**
     * Houses three node objects which are used in a single row on a GridPane
     * @param header the header for the column
     * @param description the description of what the ChoiceBox field (user input) is used for
     * @param values add any values into the choice box
     * @Note this is the lower level object used in InteractiveTabInputPane's
     */
    public InteractiveChoiceBox(String header, String description, InteractiveFieldActionEvent actionEvent, String ... values) {

        this.choiceBox = new ChoiceBox<>();
        this.fieldHeader = new Text(header);
        this.fieldDescription = new Text(description);

        this.containerPane = new StackPane();
        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        this.containerPane.getChildren().addAll(this.choiceBox,this.fieldHeader,this.fieldDescription);

        this.choiceBox.setMaxWidth(choiceBoxWidth);
        this.choiceBox.setTranslateX(choiceBoxTranslateX);

        this.containerPane.setAlignment(this.fieldHeader, Pos.CENTER_LEFT);
        this.fieldHeader.setTranslateY(fieldHeaderTranslateY);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX);
        this.fieldHeader.setWrappingWidth(fieldHeaderWrappingWidth);

        this.containerPane.setAlignment(this.fieldDescription,Pos.CENTER_LEFT);
        this.fieldDescription.setTranslateX(fieldDescTranslateX);
        this.fieldDescription.setWrappingWidth(fieldDescWrappingWidth);

        this.interactiveFieldActionEvent = actionEvent;
        this.choiceBox.setOnAction(event -> this.interactiveFieldActionEvent.fieldActionHandler(event,this.choiceBox,this.parentPane));

        // Set default info box
        this.infoBox = new InfoBox("Default message");

        // Create new input validator manager
        this.validatorManager = new ValidatorManager();

        // Add values
        this.addAllValues(List.of(values));

    }

    /**
     * Add an option (as a String) to ChoiceBox. Option cannot be named the same as an option already added
     * @param value the option to add
     * @return true if the option was added, false otherwise
     */
    public boolean addValue(String value) {

        if (this.choiceBox.getItems().contains(value)) {
            return false;
        } else {
            this.choiceBox.getItems().add(value);
            return true;
        }

    }

    /**
     * Add options (as Strings) to ChoiceBox. Option cannot be named the same as an option already added
     * @param values the options to add
     * @return true if the options were added, false otherwise
     */
    public boolean addAllValues(Collection<String> values) {
        // First check if any values already exist within the ChoiceBox
        for (String value : values) {
            if (this.choiceBox.getItems().contains(value)) {
                // If yes, return, nothing will be added
                return false;
            }
        }
        return this.choiceBox.getItems().addAll(values);
    }

    /**
     * Remove an option (as a String) from ChoiceBox
     * @param value the option to remove
     * @return true if the option was removed, false otherwise
     */
    public boolean removeValue(String value) {

        if (this.choiceBox.getItems().contains(value)) {
            this.choiceBox.getItems().remove(value);
            return true;
        } else {
            return false;
        }

    }

    /**
     * Add object to a GridPane
     * @param targetPane the GridPane object to add the object to
     * @param targetRow the target row of the GridPane object to add the object to
     */
    @Override
    public void applyPane(GridPane targetPane, int targetRow) {
        targetPane.add(this.containerPane,0,targetRow);
    }

    public Pane getPane() {
        return this.containerPane;
    }

    /**
     * Gets the current text present within the ChoiceBox
     * @return the text present as a String
     */
    @Override
    public String getInput() {
        return this.choiceBox.getValue();
    }

    @Override
    public InfoBox getInfoBox() {
        return this.infoBox;
    }

    @Override
    public void showInfo() {
        this.infoBox.applyInfoBox(this);
    }

    @Override
    public void hideInfo() {
        this.infoBox.removeInfoBox(this);
    }

    @Override
    public void setSpacing(int spacing) {
        this.choiceBox.setTranslateX(choiceBoxTranslateX+spacing);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX+(-1)*spacing);
        this.fieldDescription.setTranslateX(fieldDescTranslateX+(-1)*spacing);
    }

    @Override
    public void setDescWrappingWidth(int wrappingWidth) {
        this.fieldDescription.setWrappingWidth(wrappingWidth);
    }

    @Override
    public void addValidator(InputValidator validator) {
        this.validatorManager.add(validator);
    }

    @Override
    public void removeValidator(int index) {
        int removalIndex = 0;
        for(InputValidator validator : this.validatorManager) {
            if (removalIndex == index) {
                this.validatorManager.remove(validator);
                break;
            }
            removalIndex++;
        }
    }

    @Override
    public boolean validateField() throws ValidationException {
        return this.validatorManager.validateInput(this.getInput());
    }

    @Override
    public void setInteractivePane(InteractiveInputPane pane) {
        this.parentPane = pane;
    }

    @Override
    public void setValue(String value) {
        this.choiceBox.getSelectionModel().select(value);
    }

}
