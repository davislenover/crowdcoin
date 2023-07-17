package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

/**
 * Factory class for creation of {@link ExportBehaviour}
 */
public class ExportBehaviourFactory {

    InteractivePane pane;
    PopWindow window;
    SQLTable table;
    ModelClass modelClass;

    public ExportBehaviourFactory(InteractivePane pane, PopWindow window, SQLTable table, ModelClass modelClass) {
        this.pane = pane;
        this.window = window;
        this.table = table;
        this.modelClass = modelClass;
    }

    /**
     * Constructs a given ExportBehaviour given the behaviour name as a String
     * @param behaviour the behaviour name as a String. Must match exactly one of the enum Behaviours from {@link GeneralExportBehaviours}
     * @return an ExportBehaviour object if the behaviour String matches an enum, null otherwise
     */
    public ExportBehaviour constructExportBehaviour(String behaviour) {

        Class behaviourClass = GeneralExportBehaviours.valueOf(behaviour).getExportBehaviourClass();

        try {
            return (ExportBehaviour) behaviourClass.getConstructors()[0].newInstance(this.pane,this.window,this.table,this.modelClass);
        } catch (Exception e) {
            // TODO Error handling
        }

        return null;

    }

}
