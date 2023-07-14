package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.Interactive.Field;
import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.Observe.EventType;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

public class ViewTableRowEvent implements TabActionEvent {

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table) {

        try {

            List<String> columnNames = new ArrayList<>();

            // Get selected row from TableView
            List<Object> selectedRow = columnContainer.getSelectedRow();
            TableView<ModelClass> tableView = columnContainer.iterator().next().getTableView();
            // Save row index to re-application later
            int selectedRowIndex = tableView.getSelectionModel().getSelectedIndex();
            // Get ModelClass to get column names (for headers in InputField)
            ObservableList<TableColumn<ModelClass,?>> columns = columnContainer.iterator().next().getTableView().getColumns();

            // Get fields from table, create InputFields for them
            List<InputField> fieldsToAdd = new ArrayList<>();
            for (int index = 0; index < selectedRow.size(); index++) {
                String columnName = columns.get(index).getText();
                columnNames.add(columnName);
                InputField newField = new InteractiveTextField(columns.get(index).getText(),"Value for the specified column",(event, field, pane1) -> {return;});
                newField.setValue(selectedRow.get(index).toString());
                newField.setOrder(index);
                fieldsToAdd.add(newField);

            }

            // Clear all already present fields and add new ones
            pane.clearAllInputFields();
            pane.addAllInputFields(fieldsToAdd);

            List<String> savedInput = new ArrayList<>();
            savedInput.addAll(pane.getAllInput());

            // Add Edit/Remove buttons
            SubmitField editRowButton = new InteractiveButton("Submit edits to entry",(event, button, pane1) -> {

                // Setup
                List<String> columnsToChange = new ArrayList<>();
                List<String> correspondingDataToWrite = new ArrayList<>();
                boolean isWriteNeeded = false;

                // Get all input from InputFields
                List<String> inputToCheck = pane1.getAllInput();

                // Loop through all fields, check if their input differs from the storedInput (i.e., when selecting the row, storedInput has what was originally in the row)
                for (int index = 0; index < inputToCheck.size(); index++) {

                    String currentInput = inputToCheck.get(index);
                    String storedInput = savedInput.get(index);

                    // If both inputs differ, store the changed input along with it's corresponding column in lists
                    if (!currentInput.equals(storedInput)) {
                        columnsToChange.add(columnNames.get(index));
                        correspondingDataToWrite.add(currentInput);
                        isWriteNeeded = true;
                    }
                }

                if (isWriteNeeded) {
                    try {
                        // Write all the changed input to the SQL database
                        // This will trigger MODIFIED_ROW event in SQLTable
                        // Using tableview to get data to get the ModelClass guarantees getting the first column data correctly (first column is treated as an ID that should not be changed)
                        table.writeToRow(columnsToChange,correspondingDataToWrite,0,tableView.getItems().get(selectedRowIndex).getData(0).toString());
                    } catch (Exception e) {
                        // TODO Error Handling
                    }
                }

            });

            SubmitField removeRowButton = new InteractiveButton("Remove entry",(event, button, pane1) -> {return;});
            removeRowButton.setOrder(1);

            pane.clearAllSubmitFields();
            pane.addSubmitField(editRowButton);
            pane.addSubmitField(removeRowButton);

            // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has been changes, thus, update those changes to the screen
            pane.notifyObservers(new ModifyEvent(EventType.PANE_UPDATE));

            // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
            tableView.getSelectionModel().select(selectedRowIndex);


        } catch (Exception e) {

        }

    }
}
