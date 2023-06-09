package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

public class InteractiveTabPane extends InteractivePane {

    // The idea is to have some object which can be passed into a Tab that defines how a tab interacts with the right display (intractable display)
    /**
     * Creates an InteractiveTabPane object. InteractiveTabPane's define how a Tab interacts with the rightmost display beside the TableView (by convention).
     * This object is typically used in tandem with a Tab object (as a Tab handles invocation of applying InteractiveTabPane to GridPanes)
     * @param table the SQLTable object which communicates with the SQL database, typically defined by the Tab
     */
    public InteractiveTabPane(SQLTable table) {
        super(table);
    }

    /**
     * Add a button to InteractiveTabPane. When applyInteractivePane() is called, all buttons added will be applied to the corresponding button GridPane
     * @param buttonText the text to be displayed by the Button
     * @param eventHandler the class containing an invokable method by the Button to perform arbitrary logic upon firing of an ActionEvent by the Button. Intended to allow users to execute arbitrary logic for each button and not singular unified logic
     * @return true if a new field was added, false otherwise
     * @Note by convention, Buttons are added horizontally below the Field Grid
     */
    public boolean addButton(String buttonText, InteractiveButtonActionEvent eventHandler) {
        // Overriding addButton enables pass-through of the InteractiveTabPane to InteractiveButton
        // This way, when setting the eventHandler, the parent pane points to the TabPane rather than the Pane for correctness
        // If the arbitrary logic requires calling methods from the parent TabPane, then it can still do as TabPane is a child class of Pane
        InteractiveButton newButton = new InteractiveButton(buttonText,eventHandler,this);
        return super.addButton(newButton);
    }

}
