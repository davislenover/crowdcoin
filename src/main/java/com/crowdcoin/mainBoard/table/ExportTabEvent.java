package com.crowdcoin.mainBoard.table;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.window.ExportTabPopWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

public class ExportTabEvent implements TabActionEvent {

    private ModelClass modelClass;

    public ExportTabEvent(ModelClass modelClass) {
        this.modelClass = modelClass;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {
        PopWindow exportWindow = new ExportTabPopWindow("Export to File",table,this.modelClass,manager);
        try {
            exportWindow.start(StageManager.getStage(exportWindow));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO Error handling
        }
    }

}
