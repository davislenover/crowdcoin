package com.crowdcoin.mainBoard.Interactive;

import javafx.event.ActionEvent;
import javafx.scene.control.Control;

/**
 * The implementing class defines the logic as to what happens when a corresponding field is interacted with. The InputField class will call these methods found within a InteractiveFieldActionEvent
 */
public interface InteractiveFieldActionEvent {

    /**
     * The action performed on a field action event. Note this method is automatically triggered via a InteractiveField
     * @param event the event which caused the invocation of this method. Note this is a JavaFX action event
     * @param field the field which was interacted with (a child class of the Control JavaFX class)
     * @param pane the pane parent which the field will be utilized in
     */
    void fieldActionHandler(ActionEvent event, Control field, InteractivePane pane);

}
