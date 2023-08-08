package com.crowdcoin.exceptions.handler;

import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputPrintField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.stage.Stage;

public class FatalExceptionWindow extends PopWindow {

    private String generalMsg = "An unexpected error has occurred";
    private String excMsgPrefix = "Exception Message: ";
    private String stkTracePrefix = "Exception Stack Trace (Technical jargon): ";
    private String action = "Please report this error to an administrator and restart the program";
    private Throwable exception;

    public FatalExceptionWindow(String windowName, Throwable exception) {
        super(windowName);
        this.exception = exception;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractiveWindowPane pane = super.getWindowPane();

        OutputField generalMsg = new OutputPrintField(this.generalMsg);

        OutputField excMsg = new OutputPrintField(excMsgPrefix + this.exception.getMessage());
        excMsg.setOrder(1);

        OutputField stkTrace = new OutputPrintField(this.stkTracePrefix + this.stackTraceToString(this.exception));
        stkTrace.setOrder(2);

        OutputField actionOut = new OutputPrintField(this.action);
        actionOut.setOrder(3);

        pane.addOutputField(generalMsg);
        pane.addOutputField(excMsg);
        pane.addOutputField(stkTrace);
        pane.addOutputField(actionOut);

        SubmitField okBtn = new InteractiveButton("Ok",(event, button, pane1) -> {super.closeWindow();});
        pane.addSubmitField(okBtn);

        super.start(stage);

    }

    private String stackTraceToString(Throwable throwable) {
        StringBuilder traceBuilder = new StringBuilder();
        for (StackTraceElement element : throwable.getStackTrace()) {
            traceBuilder.append(element.toString());
            traceBuilder.append("\n");
        }
        return traceBuilder.toString();
    }


}
