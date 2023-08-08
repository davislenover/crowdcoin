package com.crowdcoin.mainBoard.Interactive.input;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class InteractiveLinkField implements InputField {

    public InteractiveLinkField(String linkMessage, InteractiveFieldActionEvent event) {

    }

    @Override
    public void setOrder(int order) {

    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public void applyPane(GridPane targetPane, int targetRow) {

    }

    @Override
    public Pane getPane() {
        return null;
    }

    @Override
    public String getInput() {
        return null;
    }

    @Override
    public InfoBox getInfoBox() {
        return null;
    }

    @Override
    public void showInfo() {

    }

    @Override
    public void hideInfo() {

    }

    @Override
    public void setValue(String value) {

    }

    @Override
    public void setSpacing(int spacing) {

    }

    @Override
    public void setDescWrappingWidth(int wrappingWidth) {

    }

    @Override
    public void setHeaderWrappingWidth(int wrappingWidth) {

    }

    @Override
    public void setHeaderDescVerticalSpacing(int spacing) {

    }

    @Override
    public void addValidator(InputValidator validator) {

    }

    @Override
    public void removeValidator(int index) {

    }

    @Override
    public boolean validateField() throws ValidationException {
        return false;
    }

    @Override
    public void setInteractivePane(InteractivePane pane) {

    }
}
