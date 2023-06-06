package com.crowdcoin.mainBoard.Interactive;

import com.crowdcoin.networking.sqlcom.data.SQLTable;

public class InteractiveWindowPane extends InteractiveTabPane {

    /**
     * Creates an InteractiveTabPane object. InteractiveTabPane's define how a Tab interacts with the rightmost display beside the TableView (by convention).
     * This object is typically used in tandem with a Tab object (as a Tab handles invocation of applying InteractiveTabPane to GridPanes)
     *
     * @param table the SQLTable object which communicates with the SQL database, typically defined by the Tab
     */
    public InteractiveWindowPane(SQLTable table) {
        super(table);
    }

}
