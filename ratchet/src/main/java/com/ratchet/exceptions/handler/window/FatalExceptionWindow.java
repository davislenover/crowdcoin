package com.ratchet.exceptions.handler.window;

import com.ratchet.interactive.InteractiveWindowPane;
import com.ratchet.interactive.output.OutputField;
import com.ratchet.interactive.output.OutputLinkField;
import com.ratchet.interactive.output.OutputPrintField;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.ratchet.window.InfoPopWindow;
import com.ratchet.window.PopWindow;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FatalExceptionWindow extends PopWindow {

    private String generalMsg = "An unexpected error has occurred";
    private String excMsgPrefix = "Exception Message: ";
    private String stkTracePrefix = "Exception Stack Trace (Technical jargon): ";
    private String action = "Please report this error to an administrator and restart the program";
    private String getStkTrace = "Show Stack Trace (Technical Jargon)";
    private Throwable exception;
    private int fontSize = 20;
    private int fontWidth = 7;
    private int extraPadding = 100;

    private int windowHeight = 300;
    private int windowWidth = 300;

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

        OutputField stkTrace = new OutputLinkField(getStkTrace,(event, field, pane1) -> {
            InfoPopWindow stkTraceWindow = new InfoPopWindow("Stack Trace Log");
            String stkTraceString = stackTraceToString(exception);
            stkTraceWindow.setInfoMessage(stkTraceString);
            int maxTextWidth = this.calculateStackTraceWindowWidth(stkTraceString);
            stkTraceWindow.setInfoWrappingWidth(maxTextWidth);
            stkTraceWindow.setOkButtonAction((event1, button, pane2) -> stkTraceWindow.closeWindow());
            try {
                stkTraceWindow.start(new Stage());
                stkTraceWindow.setWindowWidth(maxTextWidth+this.extraPadding);
                stkTraceWindow.setWindowHeight(this.calculateStackTraceWindowHeight(stkTraceString));
                stkTraceWindow.updateWindow();
            } catch (Exception e) {
                // TODO
            }
        });
        stkTrace.setValueWrappingWidth(400);
        stkTrace.setOrder(2);

        OutputField actionOut = new OutputPrintField(this.action);
        actionOut.setOrder(3);

        pane.addOutputField(generalMsg);
        pane.addOutputField(excMsg);
        pane.addOutputField(stkTrace);
        pane.addOutputField(actionOut);

        SubmitField okBtn = new InteractiveButton("Ok",(event, button, pane1) -> {
            super.closeWindow();
            Platform.exit();
        });
        pane.addSubmitField(okBtn);

        super.setWindowHeight(this.windowHeight);
        super.setWindowWidth(this.windowWidth);

        super.start(stage);

    }

    private int calculateStackTraceWindowHeight(String stackTrace) {
        return stackTrace.split("\n").length*this.fontSize;
    }

    private int calculateStackTraceWindowWidth(String stackTrace) {
        int maxWidth = 0;
        for (String line : stackTrace.split("\n")) {
            int length = line.length()*this.fontWidth;
            if (length > maxWidth) {
                maxWidth = length;
            }
        }
        return maxWidth;
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
