package com.crowdcoin.mainBoard.Interactive.output;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

public class OutputPrintField implements OutputField {

    private StackPane containerPane;
    private Text messageText;
    private int order;

    public OutputPrintField(String message) {
        this.order = 0;
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
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    @Override
    public void setValue(String value) {

    }
}
