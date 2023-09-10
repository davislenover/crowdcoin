package com.ratchet.interactive.output;

import com.ratchet.interactive.InteractivePane;
import com.ratchet.interactive.output.OutputField;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OutputPrintField implements OutputField {

    // Defaults
    private static int textWrappingWidth = 200;
    private static int textTranslateX = 45;

    private StackPane containerPane;
    private Text messageText;
    private int order;

    private InteractivePane parentPane;

    public OutputPrintField(String message) {

        this.messageText = new Text(message);
        this.containerPane = new StackPane();

        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        // By default, items are added to the center of the StackPane and the node (as in the JavaFX object like TextField or Text) will be stretched to fit the entire available space
        this.containerPane.getChildren().add(this.messageText);
        StackPane.setAlignment(this.messageText,Pos.CENTER_LEFT);

        // Set wrapping width
        this.messageText.setWrappingWidth(textWrappingWidth);
        this.messageText.setTextAlignment(TextAlignment.CENTER);
        this.messageText.setTranslateX(textTranslateX);

        this.order = 0;

    }

    @Override
    public void applyPane(GridPane targetPane, int targetRow) {
        targetPane.add(this.containerPane, 0, targetRow);
    }

    @Override
    public Pane getPane() {
        return this.containerPane;
    }

    @Override
    public void setInteractivePane(InteractivePane pane) {
        this.parentPane = pane;
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
        this.messageText.setText(value);
    }

    @Override
    public void setValueWrappingWidth(double width) {
        this.messageText.setWrappingWidth(width);
    }
}
