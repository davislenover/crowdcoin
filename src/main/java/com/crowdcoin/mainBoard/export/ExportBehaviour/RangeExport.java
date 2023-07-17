package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.ComparatorValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.TypeValidator;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.ModelClassFactory;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.util.ArrayList;
import java.util.List;

public class RangeExport implements ExportBehaviour {


    private InteractivePane pane;
    private PopWindow window;
    private SQLTable sqlTable;
    private ModelClass modelClass;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public RangeExport(InteractivePane pane, PopWindow window, SQLTable table, ModelClass modelClass) {
        this.pane = pane;
        this.window = window;
        this.sqlTable = table;
        this.modelClass = modelClass;
    }

    @Override
    public List<String> getColumns() {
        return sqlTable.getColumnNames();
    }

    /**
     * @param params the starting (index 0) and ending index (index 1) (inclusive) of the entries to return
     */
    @Override
    public List<List<String>> getEntries(Object ... params) {
        // Setup
        ModelClassFactory factory = new ModelClassFactory();
        List<String> columnNames = this.sqlTable.getRawColumnNames();
        int startingIndex = Integer.valueOf(params[0].toString());
        int endingIndex = Integer.valueOf(params[1].toString());
        int numOfRows = endingIndex-startingIndex;

        List<List<String>> entries = new ArrayList<>();

        try {
            // Get a group of rows
            List<List<Object>> rows = this.sqlTable.getRawRows(startingIndex,numOfRows,0,columnNames.size()-1);
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
        InputField field = new InteractiveTextField("Starting Entry","The number of the starting entry (inclusive), starting from 0",(event, field1, pane1) -> {return;});
        field.addValidator(new LengthValidator(1));
        field.addValidator(new TypeValidator(Integer.class));

        InputField field2 = new InteractiveTextField("Ending Entry","The number of the ending entry (exclusive)",(event, field1, pane1) -> {return;});
        field2.addValidator(new LengthValidator(1));
        field2.addValidator(new TypeValidator(Integer.class));

        field2.addValidator(new ComparatorValidator((input, inputUnused) -> {

            if (Integer.valueOf(input) >= Integer.valueOf(field.getInput())) {
                return 1;
            } else {
                return -1;
            }
            // Add onto context
        }," starting entry"));

        pane.addInputField(field);
        pane.addInputField(field2);
        this.window.setWindowHeight(600);


    }
}
