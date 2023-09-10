package com.ratchet.interactive;

import com.ratchet.interactive.submit.SubmitField;
import com.ratchet.interactive.InteractivePane;

/**
 * InteractiveTabPane is an extension of InteractivePane. TabPanes dictate how the user interacts with the right side of a Tab
 */
public class InteractiveTabPane extends InteractivePane {

    // The idea is to have some object which can be passed into a Tab that defines how a tab interacts with the right display (intractable display)
    /**
     * Creates an InteractiveTabPane object. InteractiveTabPane's define how a Tab interacts with the rightmost display beside the TableView (by convention).
     * This object is typically used in tandem with a Tab object (as a Tab handles invocation of applying InteractiveTabPane to GridPanes)
     */
    public InteractiveTabPane() {
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
