package com.ratchet.interactive.input;

import com.ratchet.interactive.InteractiveFieldActionEvent;
import com.ratchet.system.RatchetSystem;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.regex.Pattern;

/**
 * A field allowing the input of directories. Note that when selecting a directory, this field will hide the root stage set within {@link RatchetSystem}
 */
public class InteractiveDirectoryField extends InteractiveTextField {
    private static int linkTextTranslateX = 320;
    private Hyperlink fileActionLink;
    private Stage stage;

    /**
     * Houses three node objects which are used in a single row on a GridPane
     *
     * @param header      the header for the column
     * @param description the description of what the text field (user input) is used for
     * @param actionEvent
     * @Note this is the lower level object used in InteractivePane's
     */
    public InteractiveDirectoryField(String header, String description, InteractiveFieldActionEvent actionEvent, Stage stage) {
        super(header, description, actionEvent);
        this.stage = stage;
        Pane containnerPane = super.getPane();
        this.fileActionLink = new Hyperlink("Browse");
        this.fileActionLink.setOnAction(new FileLinkAction("Test",this));
        containnerPane.getChildren().add(this.fileActionLink);
        this.fileActionLink.setTranslateX(linkTextTranslateX);

    }

    @Override
    public void setSpacing(int spacing) {
        this.fileActionLink.setTranslateX(linkTextTranslateX+spacing);
        super.setSpacing(spacing);
    }

    @Override
    public String getInput() {
        String returnString = "";
        String rawInput = super.getInput();

        String[] array = rawInput.split(Pattern.quote(File.separator));

        for (int index = 0; index < array.length; index++) {
            if (index == (array.length - 1)) {
                returnString+=array[index];
            } else {
                returnString+=array[index]+",";
            }

        }
        return returnString;
    }

    private class FileLinkAction implements EventHandler {

        private String windowName;
        private InteractiveTextField textField;

        public FileLinkAction(String windowName,InteractiveTextField textField) {
            this.windowName = windowName;
            this.textField = textField;
        }

        @Override
        public void handle(Event event) {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle(this.windowName);
            // To avoid a user switching tabs while selecting a directory, hide the MainBoard window
            // This will keep the Export PopWindow open however, it will be blocked due to the directory selector
            // Thus one must close the directory window before continuing
            RatchetSystem.getRootStage().hide();
            File choosenFile = chooser.showDialog(stage.getScene().getWindow());
            if (choosenFile != null) {
                textField.setValue(choosenFile.getAbsolutePath());
            }
            RatchetSystem.getRootStage().show();
        }
    }


}
