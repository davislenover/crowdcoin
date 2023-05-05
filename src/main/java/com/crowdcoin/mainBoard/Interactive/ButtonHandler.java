package com.crowdcoin.mainBoard.Interactive;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

/**
 * The implementing class defines the logic as to what happens when a corresponding button is interacted with. The ButtonCombo class will call these methods found within a ButtonHandler
 */
public interface ButtonHandler {

    /**
     * The action performed on a button action event. Note this method is automatically triggered via a ButtonCombo
     * @param event the event which caused the invocation of this method
     * @param button the button which was interacted with
     * @param pane the pane parent which the button will be utilized in
     */
    void handleButtonClick(ActionEvent event, Button button, InteractivePane pane);

}
