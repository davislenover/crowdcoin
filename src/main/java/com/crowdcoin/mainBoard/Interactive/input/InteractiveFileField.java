package com.crowdcoin.mainBoard.Interactive.input;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.window.PopWindow;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class InteractiveFileField extends InteractiveTextField {
    private static int linkTextTranslateX = 320;
    private Hyperlink fileActionLink;

    /**
     * Houses three node objects which are used in a single row on a GridPane
     *
     * @param header      the header for the column
     * @param description the description of what the text field (user input) is used for
     * @param actionEvent
     * @Note this is the lower level object used in InteractivePane's
     */
    public InteractiveFileField(String header, String description, InteractiveFieldActionEvent actionEvent) {
        super(header, description, actionEvent);

        Pane containnerPane = super.getPane();
        this.fileActionLink = new Hyperlink("Browse");
        this.fileActionLink.setOnAction(new FileLinkAction("Test"));
        containnerPane.getChildren().add(this.fileActionLink);
        this.fileActionLink.setTranslateX(linkTextTranslateX);

    }

    @Override
    public void setSpacing(int spacing) {
        this.fileActionLink.setTranslateX(linkTextTranslateX+spacing);
        super.setSpacing(spacing);
    }

    private class FileLinkAction implements EventHandler {

        private String windowName;

        public FileLinkAction(String windowName) {
            this.windowName = windowName;
        }

        @Override
        public void handle(Event event) {
            FileChooser chooser = new FileChooser();
            chooser.setTitle(this.windowName);
            File choosenFile = chooser.showSaveDialog(StageManager.getStage(this));
            System.out.println(choosenFile);
        }
    }


}
