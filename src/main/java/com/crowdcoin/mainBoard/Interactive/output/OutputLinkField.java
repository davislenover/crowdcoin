package com.crowdcoin.mainBoard.Interactive.output;

import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class OutputLinkField implements OutputField {

    // Defaults
    private static int textWrappingWidth = 200;
    private static int textTranslateX = 45;

    private StackPane containerPane;
    private Hyperlink messageText;
    private int order;
    private InteractivePane parentPane;

    public OutputLinkField(String linkMessage, InteractiveFieldActionEvent eventToInvoke) {
        this.messageText = new Hyperlink(linkMessage);
        this.containerPane = new StackPane();

        this.containerPane.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        // By default, items are added to the center of the StackPane and the node (as in the JavaFX object like TextField or Text) will be stretched to fit the entire available space
        this.containerPane.getChildren().add(this.messageText);
        StackPane.setAlignment(this.messageText, Pos.CENTER_LEFT);

        // Set wrapping width
        this.messageText.setMaxWidth(textWrappingWidth);
        this.messageText.setTextAlignment(TextAlignment.CENTER);
        this.messageText.setTranslateX(textTranslateX);
        // Set action to invoke when clicking text
        this.messageText.setOnAction(event -> eventToInvoke.fieldActionHandler(event,this.messageText,this.parentPane));

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

    /**
     * Sets the action to invoke when clicking the given text
     * @param eventToInvoke the given InteractiveFieldActionEvent
     */
    public void setActionEvent(InteractiveFieldActionEvent eventToInvoke) {
        this.messageText.setOnAction(event -> eventToInvoke.fieldActionHandler(event,this.messageText,this.parentPane));
    }
}
