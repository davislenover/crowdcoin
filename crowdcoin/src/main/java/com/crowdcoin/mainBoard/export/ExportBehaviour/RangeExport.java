package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.ratchet.interactive.InteractivePane;
import com.ratchet.interactive.input.InputField;
import com.ratchet.interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.modelClass.DynamicModelClass;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.ratchet.interactive.input.validation.ComparatorValidator;
import com.ratchet.interactive.input.validation.GreaterComparatorValidator;
import com.ratchet.interactive.input.validation.LengthValidator;
import com.ratchet.interactive.input.validation.TypeValidator;
import com.ratchet.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;

import java.util.ArrayList;
import java.util.List;

public class RangeExport implements ExportBehaviour {


    private InteractivePane pane;
    private PopWindow window;
    private SQLTableReader tableReader;

    private String isReadablePerm = IsReadable.class.getSimpleName();

    public RangeExport(InteractivePane pane, PopWindow window, SQLTableReader tableReader) {
        this.pane = pane;
        this.window = window;
        this.tableReader = tableReader;
    }

    @Override
    public List<String> getColumns() {
        try {
            List<String> columnNames = new ArrayList<>();
            ModelClass klass = tableReader.getCurrentModelClassSet().get(0);
            for (Column column : klass.getColumns()) {
                if (column.checkPermissionValue(isReadablePerm)) {
                    if (column.isVariable()) {
                        columnNames.addAll(DynamicModelClass.getAllVariableNames(klass,column));
                    } else {
                        columnNames.add(column.getColumnName());
                    }
                }
            }
            return columnNames;
        } catch (Exception e) {
            // TODO Error handling
        }
        return null;
    }

    /**
     * @param params the starting (index 0) and ending index (index 1) (inclusive) of the entries to return
     */
    @Override
    public List<List<String>> getEntries(Object ... params) {
        // Setup
        int startingIndex = Integer.valueOf(params[0].toString());
        // To the user, the range starts at 1 and not 0 thus, index "1" is 0
        startingIndex = startingIndex-1;
        int endingIndex = Integer.valueOf(params[1].toString());
        endingIndex = endingIndex-1;
        int numOfRows = endingIndex-startingIndex;

        List<List<String>> entries = new ArrayList<>();

        try {

            // Get a group of rows
            List<ModelClass> modelClasses = this.tableReader.getRows(startingIndex,numOfRows);
            // Loop through one row at a time, construct it into one entry list and add that to the entries list
            for (ModelClass row : modelClasses) {
                List<String> newEntry = new ArrayList<>();
                for (Column column : row.getColumns()) {
                    if (column.checkPermissionValue(isReadablePerm)) {
                        if (column.isVariable()) {
                            for (Object data : DynamicModelClass.getAllVariableData(row,column)) {
                                newEntry.add(data.toString());
                            }
                        } else {
                            newEntry.add(row.getData(column.getColumnName()).toString());
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
        InputField field = new InteractiveTextField("Starting Entry","The number of the starting entry (inclusive), starting from 1",(event, field1, pane1) -> {return;});
        field.addValidator(new LengthValidator(1));
        field.addValidator(new ComparatorValidator((input, inputUnused) -> {
            return Integer.compare(Integer.valueOf(input),Integer.valueOf(1));
            // Add onto context
        }," 1"));
        field.addValidator(new TypeValidator(Integer.class));

        InputField field2 = new InteractiveTextField("Ending Entry","The number of the ending entry (exclusive)",(event, field1, pane1) -> {return;});
        field2.addValidator(new LengthValidator(1));
        field2.addValidator(new TypeValidator(Integer.class));
        // Ending entry must be greater than the starting entry
        field2.addValidator(new GreaterComparatorValidator((input, inputUnused) -> {
            return Integer.compare(Integer.valueOf(input),Integer.valueOf(field.getInput()));
            // Add onto context
        },"starting entry"));

        pane.addInputField(field);
        pane.addInputField(field2);
        this.window.setWindowHeight(600);


    }
}
