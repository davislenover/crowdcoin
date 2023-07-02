package com.crowdcoin.mainBoard.Interactive;

/**
 * InteractiveWindowInputPane is an extension of InteractiveInputPane. WindowPanes dictate how the user interacts with a PopWindow
 */
public class InteractiveWindowInputPane extends InteractiveInputPane {

    /**
     * Creates an InteractiveWindowInputPane object. InteractiveWindowInputPane's define how a user acts with a specific pop up window (by convention).
     * This object is typically used in tandem with a PopWindow object (as a PopWindow handles invocation of applying InteractiveWindowInputPane to GridPanes)
     */
    public InteractiveWindowInputPane() {
    }

    /**
     * Add a button to InteractiveWindowInputPane. When applyInteractivePane() is called, all buttons added will be applied to the corresponding button GridPane
     * @param buttonText the text to be displayed by the Button
     * @param eventHandler the class containing an invokable method by the Button to perform arbitrary logic upon firing of an ActionEvent by the Button. Intended to allow users to execute arbitrary logic for each button and not singular unified logic
     * @return true if a new field was added, false otherwise
     * @Note by convention, Buttons are added horizontally below the Field Grid
     */
    public boolean addButton(String buttonText, InteractiveButtonActionEvent eventHandler) {
        // Overriding addButton enables pass-through of the InteractiveWindowInputPane to InteractiveButton
        // This way, when setting the eventHandler, the parent pane points to the WindowPane rather than the parent Pane for correctness
        // If the arbitrary logic requires calling methods from the parent TabPane, then it can still do as WindowPane is a child class of parent Pane
        InteractiveButton newButton = new InteractiveButton(buttonText,eventHandler,this);
        return super.addButton(newButton);
    }

}
