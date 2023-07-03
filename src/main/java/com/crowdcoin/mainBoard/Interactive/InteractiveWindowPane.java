package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;

/**
 * InteractiveWindowPane is an extension of InteractivePane. WindowPanes dictate how the user interacts with a PopWindow
 */
public class InteractiveWindowPane extends InteractivePane {

    /**
     * Creates an InteractiveWindowPane object. InteractiveWindowPane's define how a user acts with a specific pop up window (by convention).
     * This object is typically used in tandem with a PopWindow object (as a PopWindow handles invocation of applying InteractiveWindowPane to GridPanes)
     */
    public InteractiveWindowPane() {
    }

    /**
     * Add an SubmitField to the InteractivePane. Also used by child classes to add SubmitFields to parent class. Sets the parent pane of the SubmitField to this instance of InteractivePane. By convention, SubmitFields are added in a row at the bottom of the window in a GridPane (each column holds a SubmitField). The order at which they are added is determined by each object's order property.
     * It is imperative that each button within this list has a successive order (i.e., no SubmitField skips an order number)
     * @param newField the field to add as an SubmitField object
     * @return true if the collection was modified as a result of invocation of this method, false otherwise.
     */
    public boolean addSubmitField(SubmitField newField) {
        // Override method to set InteractivePane to this child class of InteractivePane for correctness
        if (super.addSubmitField(newField)) {
            newField.setInteractivePane(this);
            return true;
        }
        return false;
    }

}
