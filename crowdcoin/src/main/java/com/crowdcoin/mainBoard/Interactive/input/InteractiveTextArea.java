package com.crowdcoin.mainBoard.Interactive.input;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.ValidatorManager;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class InteractiveTextArea implements InputField {

    // Defaults

    // TextArea (user input)
    private static int textAreaWidth = 200;
    private static int textAreaTranslateX = 200;

    // TextArea header
    private static int fieldHeaderTranslateX = 20;
    private static int fieldHeaderTranslateY = -30;
    private static int fieldHeaderWrappingWidth = 100;

    // TextArea description
    private static int fieldDescTranslateX = 20;
    private static int fieldDescWrappingWidth = 180;

    private StackPane containerPane;
    private TextArea textArea;
    private Text fieldHeader;
    private Text fieldDescription;

    // Event
    private InteractiveFieldActionEvent interactiveFieldActionEvent;
    private InteractivePane parentPane;

    // Info
    private InfoBox infoBox;

    // Input validators
    private ValidatorManager validatorManager;

    private int order;

    /**
     * Houses three node objects which are used in a single row on a GridPane
     * @param header the header for the column
     * @param description the description of what the text area (user input) is used for
     * @Note this is the lower level object used in InteractivePane's
     */
    public InteractiveTextArea(String header, String description, InteractiveFieldActionEvent actionEvent) {

        this.textArea = new TextArea();
        this.fieldHeader = new Text(header);
        this.fieldDescription = new Text(description);

        this.containerPane = new StackPane();
        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        this.containerPane.getChildren().addAll(this.textArea,this.fieldHeader,this.fieldDescription);

        this.textArea.setMaxWidth(textAreaWidth);
        this.textArea.setTranslateX(textAreaTranslateX);

        this.containerPane.setAlignment(this.fieldHeader, Pos.CENTER_LEFT);
        this.fieldHeader.setTranslateY(fieldHeaderTranslateY);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX);
        this.fieldHeader.setWrappingWidth(fieldHeaderWrappingWidth);

        this.containerPane.setAlignment(this.fieldDescription,Pos.CENTER_LEFT);
        this.fieldDescription.setTranslateX(fieldDescTranslateX);
        this.fieldDescription.setWrappingWidth(fieldDescWrappingWidth);

        this.interactiveFieldActionEvent = actionEvent;
        this.textArea.textProperty().addListener(observable -> {
            // TODO Create custom action event
            actionEvent.fieldActionHandler(new ActionEvent(),textArea,this.parentPane);
        });

        // Set default info box
        this.infoBox = new InfoBox("Default message");

        // Create new input validator manager
        this.validatorManager = new ValidatorManager();

        this.order = 0;

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
     * Gets the current text present within the TextArea
     * @return the text present as a String
     */
    @Override
    public String getInput() {
        return this.textArea.getText();
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
        this.textArea.setTranslateX(textAreaTranslateX+spacing);
        this.fieldHeader.setTranslateX(fieldHeaderTranslateX+(-1)*spacing);
        this.fieldDescription.setTranslateX(fieldDescTranslateX+(-1)*spacing);
    }

    @Override
    public void setDescWrappingWidth(int wrappingWidth) {
        this.fieldDescription.setWrappingWidth(wrappingWidth);
    }

    @Override
    public void setHeaderWrappingWidth(int wrappingWidth) {
        this.fieldHeader.setWrappingWidth(wrappingWidth);
    }

    @Override
    public void setHeaderDescVerticalSpacing(int spacing) {
        this.fieldHeader.setTranslateY(fieldHeaderTranslateY+spacing);
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
    public void setInteractivePane(InteractivePane pane) {
        this.parentPane = pane;
    }

    @Override
    public void setValue(String value) {
        this.textArea.setText(value);
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
