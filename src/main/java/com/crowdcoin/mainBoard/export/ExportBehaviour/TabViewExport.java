package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.ModelClassFactory;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.ArrayList;
import java.util.List;

public class TabViewExport implements ExportBehaviour {

    private InteractivePane pane;
    private PopWindow window;
    private SQLTable sqlTable;
    private ModelClass modelClass;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public TabViewExport(InteractivePane pane, PopWindow window, SQLTable table, ModelClass modelClass) {
        this.pane = pane;
        this.window = window;
        this.sqlTable = table;
        this.modelClass = modelClass;
    }

    @Override
    public List<String> getColumns() {
        return this.sqlTable.getColumnNames();
    }

    /**
     *
     * @param params the given List of rows from the Tab to export as a List of List of Objects (index 0)
     * @return
     */
    @Override
    public List<List<String>> getEntries(Object... params) {
        // Setup
        ModelClassFactory factory = new ModelClassFactory();
        List<String> columnNames = this.sqlTable.getRawColumnNames();

        List<List<String>> entries = new ArrayList<>();

        try {
            // Get a group of rows
            List<List<Object>> rows = (List<List<Object>>) params[0];
            // Loop through one row at a time, construct it into one entry list and add that to the entries list
            for (List<Object> row : rows) {
                // Build modelClass for the given row
                ModelClass clonedClass = factory.buildClone(modelClass,row.toArray());
                List<String> newEntry = new ArrayList<>();

                for (int columnIndex = 0; columnIndex < row.size(); columnIndex++) {
                    // Add all columns with specified permissions to newEntry list
                    for (Column column : clonedClass.getColumns()) {
                        // Since column list may not be ordered, find the correct one by matching names (the names returned by SQLTable, in which each row returned by the same SQLTable would also correspond)
                        if (column.getColumnName().equals(columnNames.get(columnIndex))) {
                            // Check Perms
                            if(column.checkPermissionValue(this.isReadablePerm)) {
                                newEntry.add(clonedClass.getData(column.getColumnName()).toString());
                            }
                        }
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
