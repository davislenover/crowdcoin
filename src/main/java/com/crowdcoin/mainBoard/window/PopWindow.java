package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopWindow extends Application {

    private String windowName;
    private InteractiveWindowPane parentPane;
    private GridPane fieldPane;
    private GridPane buttonPane;

    // Default sizes
    private int buttonHeight = 30;
    private int windowWidth = 425;
    private int windowHeight = 200;

    public PopWindow(String windowName, SQLTable table) {
        this.windowName = windowName;
        this.parentPane = new InteractiveWindowPane(table);

        this.fieldPane = new GridPane();
        this.buttonPane = new GridPane();

    }

    public InteractiveWindowPane getWindowPane() {
        return this.parentPane;
    }

    @Override
    public void start(Stage stage) throws Exception {

        VBox root = new VBox();
        root.getChildren().addAll(this.fieldPane,this.buttonPane);

        // Set space between GridPanes
        root.setSpacing(10);

        // This sets fieldPane to always take up any remaining space in the vbox (as button pane will be placed at the bottom so get fieldPane to fill the rest)
        VBox.setVgrow(this.fieldPane, Priority.ALWAYS);
        VBox.setVgrow(this.buttonPane,Priority.NEVER);
        // Set buttonPane to be added at the bottom center
        root.setAlignment(Pos.BOTTOM_CENTER);

        this.parentPane.applyInteractivePane(this.fieldPane,this.buttonPane);

        // Create row constraint for button height
        RowConstraints bottomConstraint = new RowConstraints();
        bottomConstraint.setPrefHeight(this.buttonHeight);
        this.buttonPane.getRowConstraints().add(bottomConstraint);

        stage.setTitle(this.windowName);
        // Set width, height
        stage.setScene(new Scene(root,this.windowWidth,this.windowHeight));
        stage.show();

    }

    /**
     * Sets the width of pop up window.
     * @param windowWidth the window width as an Integer. Must be greater than 0
     */
    public void setWindowWidth(int windowWidth) {

        if (windowWidth <= 0) {
            throw new IllegalArgumentException("Button height cannot be less than or equal to 0");
        }

        this.windowWidth = windowWidth;
    }

    /**
     * Sets the height of pop up window.
     * @param windowHeight the window height as an Integer. Must be greater than 0
     */
    public void setWindowHeight(int windowHeight) {

        if (windowHeight <= 0) {
            throw new IllegalArgumentException("Window height cannot be less than or equal to 0");
        }

        this.windowHeight = windowHeight;
    }

    /**
     * Sets the height of the button's within the Button GridPane. This affects how much space the field pane has.
     * @param buttonHeight the button height as an Integer. Must be greater than 0
     */
    public void setButtonHeight(int buttonHeight) {

        if (buttonHeight <= 0) {
            throw new IllegalArgumentException("Button height cannot be less than or equal to 0");
        }

        this.buttonHeight = buttonHeight;
    }


}
