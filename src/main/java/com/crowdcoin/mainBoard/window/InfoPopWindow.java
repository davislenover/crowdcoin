package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.InteractiveButtonActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputPrintField;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InfoPopWindow extends PopWindow {

    private OutputField messageField;

    public InfoPopWindow(String windowName) {
        super(windowName);
        this.messageField = new OutputPrintField("This is the default message");
    }

    public void setInfoMessage(String message) {
        this.messageField.setValue(message);
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane windowPane = super.getWindowPane();
        windowPane.addOutputField(this.messageField);

        InteractiveButton OkButton = new InteractiveButton("OK", ((event, button, pane) -> {}),windowPane);
        windowPane.addButton(OkButton);
        super.setWindowHeight(150);
        super.setWindowWidth(300);

        super.start(stage);
    }
}
