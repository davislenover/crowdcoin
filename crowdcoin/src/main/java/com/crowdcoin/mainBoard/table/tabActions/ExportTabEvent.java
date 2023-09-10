package com.crowdcoin.mainBoard.table.tabActions;

import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.TableViewManager;
import com.ratchet.window.StageManager;
import com.ratchet.interactive.InteractiveTabPane;
import com.ratchet.window.WindowManager;
import com.crowdcoin.mainBoard.window.ExportTabPopWindow;
import com.ratchet.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

public class ExportTabEvent implements TabActionEvent {

    private TableViewManager tableViewManager;

    public ExportTabEvent(TableViewManager tableViewManager) {
        this.tableViewManager = tableViewManager;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {
        PopWindow exportWindow = new ExportTabPopWindow("Export to File",table,this.tableViewManager,manager);
        exportWindow.setId("Export to File");
        try {
            exportWindow.start(StageManager.getStage(exportWindow));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Error handling
        }
    }

}
