package com.crowdcoin.exceptions.handler.window;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputPrintField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.window.PopWindow;
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

        super.start(stage);

    }

}
