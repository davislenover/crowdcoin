package com.crowdcoin.format.defaultActions.interactive;

import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
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
