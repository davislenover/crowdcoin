package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;

public interface TabActionEvent {

    /**
     * Invoked when a row is selected within the corresponding Tab TableView. Intended to direct a InteractiveTabPane within a Tab object.
     * @param columnContainer the columns found within the Tab TableView object
     * @param pane the corresponding pane found within the Tab beside the TableView
     */
    void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane);

}
