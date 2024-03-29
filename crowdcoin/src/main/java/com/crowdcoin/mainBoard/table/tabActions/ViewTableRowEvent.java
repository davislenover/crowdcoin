package com.crowdcoin.mainBoard.table.tabActions;

import com.ratchet.window.StageManager;
import com.ratchet.interactive.InteractiveTabPane;
import com.ratchet.interactive.input.InputField;
import com.ratchet.interactive.input.InteractiveTextField;
import com.ratchet.interactive.input.validation.MatchValidator;
import com.ratchet.interactive.input.validation.PaneValidator;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.ratchet.window.WindowManager;
import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;
import com.ratchet.observe.ModifyEventType;
import com.ratchet.observe.ModifyEvent;
import com.ratchet.window.InfoPopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class ViewTableRowEvent implements TabActionEvent {

    SQLTable gradingTable;

    public ViewTableRowEvent(SQLTable gradingTable) {
        this.gradingTable = gradingTable;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {

        // Save the selected row
        columnContainer.setSelectedRow();

        try {

            List<String> columnNames = new ArrayList<>();
            List<String> savedInput = new ArrayList<>();

            // Get selected row from TableView
            List<Object> selectedRow = columnContainer.getSelectedRow();
            // Check to make sure a row was selected
            if (!selectedRow.isEmpty()) {
                TableView<ModelClass> tableView = columnContainer.iterator().next().getTableView();
                // Save row index to re-application later
                int selectedRowIndex = columnContainer.getCurrentSelectedRelativeIndex();
                // Get ModelClass to get column names (for headers in InputField)
                ObservableList<TableColumn<ModelClass,?>> columns = columnContainer.iterator().next().getTableView().getColumns();

                // Get fields from table, create InputFields for them
                List<InputField> fieldsToAdd = new ArrayList<>();
                for (int index = 0; index < selectedRow.size(); index++) {
                    String columnName = columns.get(index).getText();
                    columnNames.add(columnName);

                    // Set InputField action to add an Edit button to the main InteractivePane
                    InputField newField = new InteractiveTextField(columns.get(index).getText(),"Value for the specified column",(event, field, pane1) -> {

                        // Check if there exists already two buttons (indicates an edit/remove button already exists)
                        if (pane.getSubmitFieldsSize() != 2) {

                            // Add Edit/Remove buttons
                            SubmitField editRowButton = new InteractiveButton("Submit edits to entry",(event1, button, pane2) -> {

                                if (PaneValidator.isInputValid(pane2)) {
                                    // Giving pane1 (or rather the Tab InteractivePane) to the PopWindow will cause it to close whenever a PANE_UPDATE event is fired
                                    InfoPopWindow checkWindow = new InfoPopWindow("Edit Row",pane1,manager);
                                    checkWindow.setInfoMessage("Submit edits to this row?");
                                    checkWindow.setOkButtonMessage("Yes");
                                    checkWindow.setId("Edit Row");

                                    SubmitField cancelConfirmation = new InteractiveButton("No",((event2, button1, pane3) -> {
                                        checkWindow.closeWindow();
                                    }));
                                    cancelConfirmation.setOrder(1);
                                    checkWindow.getWindowPane().addSubmitField(cancelConfirmation);

                                    checkWindow.setOkButtonAction((event2, button1, pane3) -> {

                                        // Setup
                                        List<String> columnsToChange = new ArrayList<>();
                                        List<String> correspondingDataToWrite = new ArrayList<>();
                                        boolean isWriteNeeded = false;

                                        // Get all input from InputFields
                                        List<String> inputToCheck = pane1.getAllInput();

                                        // Loop through all fields, check if their input differs from the storedInput (i.e., when selecting the row, storedInput has what was originally in the row)
                                        for (int fieldIndex = 0; fieldIndex < inputToCheck.size(); fieldIndex++) {

                                            String currentInput = inputToCheck.get(fieldIndex);
                                            String storedInput = savedInput.get(fieldIndex);

                                            // If both inputs differ, store the changed input along with it's corresponding column in lists
                                            if (!currentInput.equals(storedInput)) {
                                                columnsToChange.add(columnNames.get(fieldIndex));
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

                                        checkWindow.closeWindow();

                                    });

                                    try {
                                        checkWindow.start(StageManager.getStage(checkWindow));
                                    } catch (Exception e) {
                                        // TODO Error handling
                                    }
                                }

                            });

                            editRowButton.setOrder(1);
                            pane.addSubmitField(editRowButton);

                            // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has changes, thus, update those changes to the screen
                            pane.notifyObservers(new ModifyEvent(ModifyEventType.PANE_UPDATE));

                            // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
                            tableView.getSelectionModel().select(selectedRowIndex);

                        }

                    });

                    String fieldValue = selectedRow.get(index).toString();
                    newField.setValue(fieldValue);
                    savedInput.add(fieldValue);
                    newField.setOrder(index);
                    fieldsToAdd.add(newField);

                    // Index 0 is the ID column, thus this should not be changed by the user
                    if (index == 0) {
                        newField.addValidator(new MatchValidator(fieldValue));
                    }

                }

                // Clear all already present fields and add new ones
                pane.clearAllInputFields();
                pane.addAllInputFields(fieldsToAdd);

                SubmitField removeRowButton = new InteractiveButton("Remove entry",(event, button, pane1) -> {

                    if (PaneValidator.isInputValid(pane1)) {
                        InfoPopWindow checkWindow = new InfoPopWindow("Remove Row",pane1,manager);
                        checkWindow.setInfoMessage("Remove the row? This action cannot be undone!");
                        checkWindow.setOkButtonMessage("Yes");
                        checkWindow.setId("Remove Row");

                        SubmitField cancelConfirmation = new InteractiveButton("No",((event2, button1, pane3) -> {
                            checkWindow.closeWindow();
                        }));
                        cancelConfirmation.setOrder(1);
                        checkWindow.getWindowPane().addSubmitField(cancelConfirmation);

                        checkWindow.setOkButtonAction((event1, button1, pane2) -> {

                            try {
                                // Since entry was removed, the row index is no longer valid
                                columnContainer.resetSelectedRowIndex();
                                String coinID = tableView.getItems().get(selectedRowIndex).getData(0).toString();
                                table.deleteRow(0,coinID);
                                // Delete row in grading table too
                                gradingTable.deleteRow(0,coinID);
                            } catch (Exception e) {
                                // TODO Exception handling
                            }

                            checkWindow.closeWindow();

                        });

                        try {
                            checkWindow.start(StageManager.getStage(checkWindow));
                        } catch (Exception e) {
                            // TODO Error handling
                        }
                    }

                });

                removeRowButton.setOrder(0);

                pane.clearAllSubmitFields();
                pane.addSubmitField(removeRowButton);

                // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has been changed, thus, update those changes to the screen
                pane.notifyObservers(new ModifyEvent(ModifyEventType.PANE_UPDATE));

                // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
                tableView.getSelectionModel().select(selectedRowIndex);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
