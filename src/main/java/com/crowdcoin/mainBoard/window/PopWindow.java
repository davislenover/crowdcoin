package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.mainBoard.table.Observe.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopWindow extends Application implements Observer<ModifyEvent>, Observable<WindowEvent> {

    private String windowName;
    private InteractiveWindowPane parentPane;
    private InteractivePane callerPane;
    private GridPane fieldPane;
    private GridPane buttonPane;

    // Default sizes
    private int buttonHeight = 30;
    private int windowWidth = 425;
    private int windowHeight = 200;

    // Scene
    private Stage stage;
    private Scene scene;
    private VBox root;

    private List<Observer<WindowEvent>> subscriptionList = new ArrayList<>();

    /**
     * Creates a new PopWindow object. PopWindows are used to create pop-up windows
     * @param windowName the name of the window
     * @param callerPane the invoking window with it's interactive pane. Used to react to events from the InteractivePane (such as if the pane has changed and the PopWindow needs to close)
     */
    public PopWindow(String windowName, InteractivePane callerPane) {
        this.callerPane = callerPane;
        this.windowName = windowName;
        this.parentPane = new InteractiveWindowPane();

        this.fieldPane = new GridPane();
        this.buttonPane = new GridPane();

        this.root = new VBox();
        this.scene = new Scene(root,this.windowWidth,this.windowHeight);

        callerPane.addObserver(this);

    }

    /**
     * Creates a new PopWindow object. PopWindows are used to create pop-up windows
     * @param windowName the name of the window
     */
    public PopWindow(String windowName) {
        this.windowName = windowName;
        this.parentPane = new InteractiveWindowPane();

        this.fieldPane = new GridPane();
        this.buttonPane = new GridPane();

        this.root = new VBox();
        this.scene = new Scene(root,this.windowWidth,this.windowHeight);

    }

    public InteractiveWindowPane getWindowPane() {
        return this.parentPane;
    }

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;

        this.root.getChildren().addAll(this.fieldPane,this.buttonPane);

        // Set space between GridPanes
        this.root.setSpacing(10);

        // This sets fieldPane to always take up any remaining space in the vbox (as button pane will be placed at the bottom so get fieldPane to fill the rest)
        VBox.setVgrow(this.fieldPane, Priority.ALWAYS);
        VBox.setVgrow(this.buttonPane,Priority.NEVER);
        // Set buttonPane to be added at the bottom center
        this.root.setAlignment(Pos.BOTTOM_CENTER);

        this.parentPane.applyInteractivePane(this.fieldPane,this.buttonPane);

        // Create row constraint for button height
        RowConstraints bottomConstraint = new RowConstraints();
        bottomConstraint.setPrefHeight(this.buttonHeight);
        this.buttonPane.getRowConstraints().add(bottomConstraint);

        this.stage.setTitle(this.windowName);
        // Set width, height
        this.stage.setScene(this.scene);
        this.stage.setWidth(this.windowWidth);
        this.stage.setHeight(this.windowHeight);
        this.stage.show();

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

    /**
     * Apply any updates to the window to the screen
     */
    public void updateWindow() {
        // Note that manually calling a window refresh is less computationally intensive than observing changes as if more than one change is needed, applyInteractivePane() will be invoked multiple times, wasting resources
        // Update window dimensions
        this.stage.setHeight(this.windowHeight);
        this.stage.setWidth(this.windowWidth);
        // Re-apply interactive pane (this will add any newly added buttons/fields to the window)
        this.parentPane.applyInteractivePane(this.fieldPane,this.buttonPane);
    }

    /**
     * Close window. Removes JavaFX Scene object from StageManager
     */
    public void closeWindow() {
        this.stage.close();
        // Remove stage from StageManager (if present)
        StageManager.removeStage(this);
        removeObserving();
    }

    @Override
    public void removeObserving() {
        callerPane.removeObserver(this);
    }

    @Override
    public void update(ModifyEvent event) {
        if (event.getEventType() == ModifyEventType.PANE_UPDATE) {
            this.closeWindow();
        }
    }

    @Override
    public boolean addObserver(Observer<WindowEvent> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<WindowEvent> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(WindowEvent event) {
        for (Observer<WindowEvent> observer : this.subscriptionList) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }
}
