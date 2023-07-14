package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.InteractiveButtonActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputPrintField;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

/**
 * A class for display a short message along with button actions to the screen. By default, one OutputPrintField and SubmitField (order of 0) are present within the InteractivePane upon calling {@link com.crowdcoin.mainBoard.window.InfoPopWindow#start(Stage)}
 */
public class InfoPopWindow extends PopWindow {

    private OutputField messageField;
    private String okButtonMessage = "Ok";
    private InteractiveButtonActionEvent okButtonAction = new InteractiveButtonActionEvent() {
        @Override
        public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
            return;
        }
    };

    // Default screen dimensions
    private static int windowHeight = 150;
    private static int windowWidth = 300;

    /**
     * Creates an InfoPopWindow
     * @param windowName the given window name
     */
    public InfoPopWindow(String windowName) {
        super(windowName);
        this.messageField = new OutputPrintField("This is the default message");
    }

    /**
     * Creates an InfoPopWindow with a caller pane. This window will react to changes made by the caller pane
     * @param windowName the given window name
     * @param callerPane the given windows pane which called this InfoWindow. If a PANE_UPDATE event is called, this window will close
     */
    public InfoPopWindow(String windowName, InteractivePane callerPane) {
        super(windowName, callerPane);
        this.messageField = new OutputPrintField("This is the default message");
    }

    /**
     * Sets the message to display in the OutputField. If this method is not invoke, message will default to "This is the default message"
     * @param message the given message as a String
     */
    public void setInfoMessage(String message) {
        this.messageField.setValue(message);
    }

    /**
     * Sets the message to display in the Ok button (the button created by this class). If this method is not called, message will default to "Ok"
     * @param message the given message as a String
     */
    public void setOkButtonMessage(String message) {
        this.okButtonMessage = message;
    }

    /**
     * Sets the action invoked for clicking the Ok button (the button created by this class)
     * @param okButtonAction the event to invoke
     */
    public void setOkButtonAction(InteractiveButtonActionEvent okButtonAction) {
        this.okButtonAction = okButtonAction;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane windowPane = super.getWindowPane();
        windowPane.addOutputField(this.messageField);

        SubmitField OkButton = new InteractiveButton(this.okButtonMessage,this.okButtonAction);
        windowPane.addSubmitField(OkButton);
        super.setWindowHeight(windowHeight);
        super.setWindowWidth(windowWidth);

        super.start(stage);
    }
}
