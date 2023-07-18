package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class FullExport implements ExportBehaviour {

    private InteractivePane pane;
    private PopWindow window;
    private SQLTableReader tableReader;
    private int numOfRowsPerRequest = 10;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public FullExport(InteractivePane pane, PopWindow window, SQLTableReader tableReader) {
        this.pane = pane;
        this.window = window;
        this.tableReader = tableReader;
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

    @Override
    public List<List<String>> getEntries(Object ... params) {

        // Setup
        int currentRowIndex = 0;
        boolean hasReachedEnd = false;

        List<List<String>> entries = new ArrayList<>();

        try {

            while (!hasReachedEnd) {
                // Get a group of rows
                List<ModelClass> modelClasses = this.tableReader.getRows(currentRowIndex,currentRowIndex+this.numOfRowsPerRequest);
                // Loop through one row at a time, construct it into one entry list and add that to the entries list
                for (ModelClass row : modelClasses) {
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
                // Stop if the rows list is smaller than that requested (meaning this loop has reached the end of the database table)
                if (modelClasses.size() < this.numOfRowsPerRequest) {
                    hasReachedEnd = true;
                }
                currentRowIndex+=this.numOfRowsPerRequest;
            }

        } catch (Exception e) {

        }

        return entries;
    }

    @Override
    public void applyInputFieldsOnWindow() {
        this.window.setWindowHeight(300);
    }
}
