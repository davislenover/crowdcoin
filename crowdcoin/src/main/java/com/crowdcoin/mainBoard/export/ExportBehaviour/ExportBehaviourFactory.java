package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.ratchet.interactive.InteractivePane;
import com.ratchet.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;

/**
 * Factory class for creation of {@link ExportBehaviour}
 */
public class ExportBehaviourFactory {

    InteractivePane pane;
    PopWindow window;
    SQLTableReader reader;

    public ExportBehaviourFactory(InteractivePane pane, PopWindow window, SQLTableReader tableReader) {
        this.pane = pane;
        this.window = window;
        this.reader = tableReader;
    }

    /**
     * Constructs a given ExportBehaviour given the behaviour name as a String
     * @param behaviour the behaviour name as a String. Must match exactly one of the enum Behaviours from {@link GeneralExportBehaviours}
     * @return an ExportBehaviour object if the behaviour String matches an enum, null otherwise
     */
    public ExportBehaviour constructExportBehaviour(String behaviour) {

        Class behaviourClass = GeneralExportBehaviours.valueOf(behaviour).getExportBehaviourClass();

        try {
            return (ExportBehaviour) behaviourClass.getConstructors()[0].newInstance(this.pane,this.window,this.reader);
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Error handling
        }

        return null;

    }

}
