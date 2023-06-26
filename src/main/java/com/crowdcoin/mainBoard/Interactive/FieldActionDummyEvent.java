package com.crowdcoin.mainBoard.Interactive;

import javafx.event.ActionEvent;
import javafx.scene.control.Control;

/**
 * A blank action event (that does nothing) for field actions
 */
public class FieldActionDummyEvent implements InteractiveFieldActionEvent {
    @Override
    public void fieldActionHandler(ActionEvent event, Control field, InteractivePane pane) {
        return;
    }
}
