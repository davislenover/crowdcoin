package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class PopWindow extends Application {

    private String windowName;
    private InteractiveWindowPane parentPane;
    private GridPane fieldPane;
    private GridPane buttonPane;

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
        root.getChildren().addAll(fieldPane,buttonPane);

        // Set space between GridPanes
        root.setSpacing(10);

        // This sets fieldPane to always take up any remaining space in the vbox (as button pane will be placed at the bottom so get fieldPane to fill the rest)
        VBox.setVgrow(fieldPane, Priority.ALWAYS);
        VBox.setVgrow(buttonPane,Priority.NEVER);
        // Set buttonPane to be added at the bottom center
        root.setAlignment(Pos.BOTTOM_CENTER);

        this.parentPane.applyInteractivePane(fieldPane,buttonPane);


        stage.setTitle(this.windowName);
        // Set width, height
        stage.setScene(new Scene(root,425,200));
        stage.show();

    }
}
