package com.crowdcoin.mainBoard.window;

import com.ratchet.window.StageManager;
import com.ratchet.interactive.FieldActionDummyEvent;
import com.ratchet.interactive.InteractivePane;
import com.ratchet.interactive.input.InputField;
import com.ratchet.interactive.input.InteractiveTextField;
import com.ratchet.interactive.input.validation.LengthValidator;
import com.ratchet.interactive.input.validation.PaneValidator;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.permissions.IsWriteable;
import com.crowdcoin.networking.sqlcom.Generation.Generator;
import com.crowdcoin.networking.sqlcom.Generation.IDGenerator;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.ratchet.window.InfoPopWindow;
import com.ratchet.window.PopWindow;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NewEntryPopWindow extends PopWindow {

    private SQLTable columnData;
    private SQLTable gradingData;
    private String idColumnName = "coinID";
    private static String isWriteablePermission = IsWriteable.class.getSimpleName();

    public NewEntryPopWindow(String windowName, SQLTable columnData, SQLTable gradingData) {
        super(windowName);
        this.columnData = columnData;
        this.gradingData = gradingData;
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

                    // Write a new row to grading table too
                    List<String> gradingColumnNames = new ArrayList<>() {{
                        add("coinID");
                    }};
                    List<String> gradingColumnData = new ArrayList<>() {{
                        add(coinID);
                    }};
                    gradingData.systemWriteNewRow(gradingColumnNames,gradingColumnData);

                    // Create window showing id to user
                    InfoPopWindow idWindow = new InfoPopWindow("Entry Submitted");
                    idWindow.setId("Entry Submitted");
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
                cancelConfirmation.setId("Cancel Confirmation");
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
