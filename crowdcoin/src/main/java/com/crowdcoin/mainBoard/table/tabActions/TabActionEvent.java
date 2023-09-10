package com.crowdcoin.mainBoard.table.tabActions;

import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.ratchet.interactive.InteractiveTabPane;
import com.ratchet.window.WindowManager;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.ratchet.window.PopWindow;

public interface TabActionEvent {

    /**
     * Invoked when a row is selected within the corresponding Tab TableView. Intended to direct a InteractiveTabPane within a Tab object.
     * @param columnContainer the columns found within the Tab TableView object
     * @param pane the corresponding pane found within the Tab beside the TableView
     * @param manager the corresponding WindowManager found within the Tab. It is strongly recommended to that if creating any {@link PopWindow} classes within the event, that they be instantiated with
     * the WindowManager from the given Tab
     */
    void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager);

}
