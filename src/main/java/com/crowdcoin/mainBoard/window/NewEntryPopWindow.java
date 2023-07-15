package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.CoinModel;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.TabBar;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.table.permissions.IsWriteable;
import com.crowdcoin.mainBoard.table.permissions.Permission;
import com.crowdcoin.networking.sqlcom.Generation.Generator;
import com.crowdcoin.networking.sqlcom.Generation.IDGenerator;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.stage.Stage;

import java.util.List;

public class NewEntryPopWindow extends PopWindow {

    private SQLTable columnData;
    private String idColumnName = "coinID";
    private static String isWriteablePermission = IsWriteable.class.getSimpleName();

    public NewEntryPopWindow(String windowName, SQLTable columnData) {
        super(windowName);
        this.columnData = columnData;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane windowPane = super.getWindowPane();

        int indexOfID = columnData.getColumnNames().indexOf(this.idColumnName);

        List<String> columnNames = columnData.getColumnNames(this.isWriteablePermission);
        System.out.println(columnNames.size());

        for (String column : columnNames) {
            InputField newField = new InteractiveTextField(column,"Enter data for the given column",new FieldActionDummyEvent());
            newField.setDescWrappingWidth(200);
            newField.setHeaderWrappingWidth(200);
            newField.setHeaderDescVerticalSpacing(10);
            newField.addValidator(new LengthValidator(1));
            windowPane.addInputField(newField);
        }

        SubmitField addEntry = new InteractiveButton("Add Entry",((event, button, pane) -> {

            if (PaneValidator.isInputValid(pane)) {
                // Get all fields
                List<String> fieldInput = pane.getAllInput();

                // Generate ID
                Generator<Integer,SQLTable> idGenerator = new IDGenerator(this.idColumnName);
                String coinID = String.valueOf(idGenerator.generateValue(this.columnData));
                // Add id to fieldsInput/columnNames (in its proper position as getColumnNames only returned fields that were writeable, thus most likely not all columns were added to said list)
                fieldInput.add(indexOfID,coinID);
                columnNames.add(indexOfID,this.idColumnName);

                // Since fields on-screen were placed in the same order as columnNames, use both list to write new row to SQL Table
                try {
                    columnData.systemWriteNewRow(columnNames,fieldInput);

                    // Create window showing id to user
                    InfoPopWindow idWindow = new InfoPopWindow("Entry Submitted");
                    idWindow.setInfoMessage("Entry submitted. Assigned ID: " + coinID);
                    idWindow.setOkButtonAction((event1, button1, pane1) -> idWindow.closeWindow());
                    idWindow.start(StageManager.getStage(idWindow));
                } catch (Exception e) {
                    // TODO Error handling
                }

                super.closeWindow();
            }

        }));

        SubmitField cancelAddEntry = new InteractiveButton("Cancel",((event, button, pane) -> {

            try {
                // Yes Button
                InfoPopWindow cancelConfirmation = new InfoPopWindow("Cancel Confirmation");
                cancelConfirmation.setInfoMessage("Cancel adding a new entry?");
                cancelConfirmation.setOkButtonMessage("Yes");
                cancelConfirmation.setOkButtonAction((event1, button1, pane1) -> {
                    // Close both windows on cancellation confirmation
                    cancelConfirmation.closeWindow();
                    super.closeWindow();
                });

                // No Button
                InteractivePane cancelPane =  cancelConfirmation.getWindowPane();
                SubmitField cancelButton = new InteractiveButton("No",((event1, button1, pane1) -> {
                    cancelConfirmation.closeWindow();
                }));
                cancelButton.setOrder(1);
                cancelPane.addSubmitField(cancelButton);

                cancelConfirmation.start(StageManager.getStage(cancelConfirmation));

            } catch (Exception e) {
                // TODO Error handling
            }

            }));
        cancelAddEntry.setOrder(1);
        windowPane.addSubmitField(addEntry);
        windowPane.addSubmitField(cancelAddEntry);

        super.setWindowHeight(600);

        super.start(stage);
    }
}
