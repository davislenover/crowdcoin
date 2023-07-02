package com.crowdcoin.mainBoard.Interactive.output;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class OutputPrintField implements OutputField {

    private StackPane containerPane;
    private Text messageText;

    public OutputPrintField(String message) {
        this.messageText = new Text(message);
        this.containerPane = new StackPane();
    }

    @Override
    public void applyPane(GridPane targetPane, int targetRow) {

    }

    @Override
    public Pane getPane() {
        return null;
    }

    @Override
    public void setInteractivePane(InteractivePane pane) {

    }

    @Override
    public void setValue(String value) {

    }
}
