package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.ModelClassFactory;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;

import java.util.ArrayList;
import java.util.List;

public class TabViewExport implements ExportBehaviour {

    private InteractivePane pane;
    private PopWindow window;
    private SQLTableReader tableReader;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public TabViewExport(InteractivePane pane, PopWindow window, SQLTableReader reader) {
        this.pane = pane;
        this.window = window;
        this.tableReader = reader;
    }

    @Override
    public List<String> getColumns() {
        List<String> columnNames = new ArrayList<>();
        for (Column column : tableReader.getModelClass().getColumns()) {
            if (column.checkPermissionValue(isReadablePerm)) {
                columnNames.add(column.getColumnName());
            }
        }
        return columnNames;
    }

    /**
     *
     * @param params the given List of rows from the Tab to export as a List of List of Objects (index 0)
     * @return
     */
    @Override
    public List<List<String>> getEntries(Object... params) {
        // Setup
        List<List<String>> entries = new ArrayList<>();

        try {
            // Get the current group of rows
            List<ModelClass> rows = this.tableReader.getCurrentModelClassSet();
            // Loop through one row at a time, construct it into one entry list and add that to the entries list
            for (ModelClass row : rows) {
                // Build modelClass for the given row
                List<String> newEntry = new ArrayList<>();
                for (Column column : row.getColumns()) {
                    if (column.checkPermissionValue(isReadablePerm)) {
                        newEntry.add(row.getData(column.getColumnName()).toString());
                    }
                }

                // Add entry to list
                entries.add(newEntry);
            }

        } catch (Exception e) {
            // TODO Error handling
        }

        return entries;
    }

    @Override
    public void applyInputFieldsOnWindow() {

    }
}
