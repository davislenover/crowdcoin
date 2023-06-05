package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractiveButton;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopWindow extends Application {

    private String windowName;
    private List<InputField> windowElements;
    private List<InteractiveButton> windowActionButtons;

    public PopWindow(String windowName) {
        this.windowName = windowName;
        this.windowElements = new ArrayList<>();
        this.windowActionButtons = new ArrayList<>();
    }

    public boolean addInputField(InputField field) {
        return this.windowElements.add(field);
    }

    public boolean addInteractiveButton(InteractiveButton button) {
        return this.windowActionButtons.add(button);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Base layer of window, this GridPane contains one column and X amount of rows
        // The last row is where all the buttons are
        GridPane paneRoot = new GridPane();

        // Apply input fields to GridPane
        for (int row = 0; row < this.windowElements.size(); row++) {
            this.windowElements.get(row).applyPane(paneRoot,row);
        }

        // Apply interactive buttons to bottom of GridPane
        for (InteractiveButton button : this.windowActionButtons) {
            button.applyPane(paneRoot,0, paneRoot.getRowCount());
        }

        stage.setTitle(this.windowName);
        stage.setScene(new Scene(paneRoot,300,200));
        stage.show();

    }
}
