package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class FullExport implements ExportBehaviour {

    private InteractivePane pane;
    private PopWindow window;
    private SQLTable sqlTable;
    private ModelClass modelClass;
    private int numOfRowsPerRequest = 10;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public FullExport(InteractivePane pane, PopWindow window, SQLTable table, ModelClass modelClass) {
        this.pane = pane;
        this.window = window;
        this.sqlTable = table;
        this.modelClass = modelClass;
    }

    @Override
    public List<String> getColumns() {
        return sqlTable.getColumnNames();
    }

    @Override
    public List<List<String>> getEntries() {

        // Setup
        ModelClassFactory factory = new ModelClassFactory();
        List<String> columnNames = this.sqlTable.getRawColumnNames();
        int currentRowIndex = 0;
        boolean hasReachedEnd = false;

        List<List<String>> entries = new ArrayList<>();

        try {

            while (!hasReachedEnd) {
                // Get a group of rows
                List<List<Object>> rows = this.sqlTable.getRawRows(currentRowIndex,this.numOfRowsPerRequest,0,columnNames.size()-1);
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
                // Stop if the rows list is smaller than that requested (meaning this loop has reached the end of the database table)
                if (rows.size() < this.numOfRowsPerRequest) {
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
        InputField field = new InteractiveTextField("Filename","The name to be given to the exported file",(event, field1, pane1) -> {return;});
        field.addValidator(new LengthValidator(1));
        this.pane.addInputField(field);
        this.window.setWindowHeight(300);
    }
}
