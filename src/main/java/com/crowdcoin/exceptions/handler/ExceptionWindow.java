package com.crowdcoin.exceptions.handler;

import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.output.OutputPrintField;
import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.stage.Stage;

public class ExceptionWindow extends PopWindow  {

    private String generalMsg = "An unexpected error has occurred";
    private String excMsgPrefix = "Exception Message: ";
    private String stkTracePrefix = "Exception Stack Trace (Technical jargon): ";
    private Throwable exception;
    public ExceptionWindow(String windowName, Throwable exception) {
        super(windowName);
        this.exception = exception;
    }
    @Override
    public void start(Stage stage) {

        InteractiveWindowPane pane = super.getWindowPane();

        OutputField generalMsg = new OutputPrintField(this.generalMsg);

        OutputField excMsg = new OutputPrintField(excMsgPrefix + this.exception.getMessage());
        excMsg.setOrder(1);

        OutputField stkTrace = new OutputPrintField(this.stkTracePrefix + this.stackTraceToString(this.exception));
        stkTrace.setOrder(2);

        pane.addOutputField(generalMsg);
        pane.addOutputField(excMsg);
        pane.addOutputField(stkTrace);





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
