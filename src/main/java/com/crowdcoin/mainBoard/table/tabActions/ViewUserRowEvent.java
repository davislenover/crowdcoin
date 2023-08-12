package com.crowdcoin.mainBoard.table.tabActions;

import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.MatchValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.TabActionEvent;
import com.crowdcoin.mainBoard.window.InfoPopWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLDatabaseGroup;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableGroup;

import java.util.List;

public class ViewUserRowEvent implements TabActionEvent {

    private SQLTable userDataTable;
    private SQLTable userGrantsDataTable;

    public ViewUserRowEvent(SQLTable userDataTable, SQLTable userGrantsDataTable) {
        this.userDataTable = userDataTable;
        this.userGrantsDataTable = userGrantsDataTable;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {

        pane.clearAllFields();

        // Save the selected row
        columnContainer.setSelectedRow();

        try {
            // Get selected row from TableView
            List<Object> selectedRow = columnContainer.getSelectedRow();
            String userName = selectedRow.get(0).toString();
            InputField printName = new InteractiveTextField("Username","The username of the selected user:",new FieldActionDummyEvent());
            printName.setValue(userName);
            printName.addValidator(new MatchValidator(userName));
            pane.addInputField(printName);

            SubmitField deleteUserBtn = new InteractiveButton("Remove User",(event, button, pane1) -> {

                if (PaneValidator.isInputValid(pane)) {
                    InfoPopWindow confirmationWindow = new InfoPopWindow("Confirm removal");
                    InteractivePane confirmPane = confirmationWindow.getWindowPane();
                    SubmitField cancelBtn = new InteractiveButton("Cancel",(event1, button1, pane2) -> {
                        confirmationWindow.closeWindow();
                    });
                    cancelBtn.setOrder(1);
                    confirmPane.addSubmitField(cancelBtn);
                    confirmationWindow.setInfoMessage("Remove user " + userName + "?");
                    confirmationWindow.setOkButtonAction((event1, button1, pane2) -> {

                        SQLDatabaseGroup databaseGroup = table.getDatabase().getQueryGroup();
                        SQLTableGroup userDataGroup = this.userDataTable.getQueryGroup();
                        SQLTableGroup userGrantsDataGroup = this.userGrantsDataTable.getQueryGroup();



                    });
                }
            });

            pane.addSubmitField(deleteUserBtn);

        } catch (Exception exception) {

        }


    }
}
