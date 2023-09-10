package com.ratchet.exceptions.handler.window;

import com.ratchet.interactive.InteractiveWindowPane;
import com.ratchet.interactive.output.OutputField;
import com.ratchet.interactive.output.OutputPrintField;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.ratchet.window.PopWindow;
import javafx.stage.Stage;

public class WarningExceptionWindow extends PopWindow {

    private String msg;
    public WarningExceptionWindow(String windowName, String msg) {
        super(windowName);
        this.msg = msg;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractiveWindowPane pane = super.getWindowPane();
        OutputField msgOut = new OutputPrintField(this.msg);
        pane.addOutputField(msgOut);

        SubmitField okBtn = new InteractiveButton("Ok",(event, button, pane1) -> super.closeWindow());
        pane.addSubmitField(okBtn);

        super.setWindowWidth(300);
        super.start(stage);

    }

}
