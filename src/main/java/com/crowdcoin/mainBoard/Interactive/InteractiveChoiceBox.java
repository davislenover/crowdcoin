package com.crowdcoin.mainBoard.Interactive;

import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

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
    private static int fieldDescWrappingWidth = 200;

    private StackPane containerPane;
    private ChoiceBox<String> choiceBox;
    private Text fieldHeader;
    private Text fieldDescription;

    public InteractiveChoiceBox(String header, String description) {

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

    }

    public boolean addValue(String value) {

        if (this.choiceBox.getItems().contains(value)) {
            return false;
        } else {
            this.choiceBox.getItems().add(value);
            return true;
        }

    }

    public boolean removeValue(String value) {

        if (this.choiceBox.getItems().contains(value)) {
            this.choiceBox.getItems().remove(value);
            return true;
        } else {
            return false;
        }

    }

    @Override
    public void applyPane(GridPane targetPane, int targetRow) {
        targetPane.add(this.containerPane,0,targetRow);
    }

    @Override
    public String getInput() {
        return this.choiceBox.getValue();
    }
}
