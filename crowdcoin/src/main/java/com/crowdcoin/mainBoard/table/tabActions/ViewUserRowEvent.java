package com.crowdcoin.mainBoard.table.tabActions;

import com.ratchet.window.StageManager;
import com.ratchet.exceptions.handler.ExceptionGuardian;
import com.ratchet.exceptions.handler.GeneralExceptionHandler;
import com.crowdcoin.exceptions.handler.SQLExceptionHandler;
import com.ratchet.interactive.FieldActionDummyEvent;
import com.ratchet.interactive.InteractivePane;
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
import com.ratchet.observe.ModifyEvent;
import com.ratchet.observe.ModifyEventType;
import com.ratchet.window.InfoPopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLDatabaseGroup;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableGroup;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.List;

public class ViewUserRowEvent implements TabActionEvent {

    private SQLTable userDataTable;

    public ViewUserRowEvent(SQLTable userDataTable) {
        this.userDataTable = userDataTable;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {

        // Save the selected row
        columnContainer.setSelectedRow();

        try {
            TableView<ModelClass> tableView = columnContainer.iterator().next().getTableView();
            List<Object> selectedRow = columnContainer.getSelectedRow();

            if (!selectedRow.isEmpty()) {
                pane.clearAllFields();
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
                            SQLTableGroup userGrantsDataGroup = table.getQueryGroup();

                            databaseGroup.removeUser(userName);
                            databaseGroup.removeColumn(this.userDataTable.getTableName(),this.getUserDataFullColumnName(userName));
                            userGrantsDataGroup.deleteRow(0,userName);

                            try {
                                databaseGroup.executeAllQueries(databaseGroup,userGrantsDataGroup);
                                confirmationWindow.closeWindow();
                            } catch (SQLException exception) {
                                ExceptionGuardian<SQLException> guardian = new ExceptionGuardian<>(new SQLExceptionHandler());
                                guardian.handleException(exception);
                                // TODO Error handling
                            }

                        });

                        try {
                            confirmationWindow.start(StageManager.getStage(confirmationWindow));
                        } catch (Exception exception) {
                            ExceptionGuardian<Exception> guardian = new ExceptionGuardian<>(new GeneralExceptionHandler());
                            guardian.handleException(exception);
                        }

                    }
                });

                pane.addSubmitField(deleteUserBtn);
            }

            // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has been changed, thus, update those changes to the screen
            pane.notifyObservers(new ModifyEvent(ModifyEventType.PANE_UPDATE));

            // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
            tableView.getSelectionModel().select(columnContainer.getCurrentSelectedRelativeIndex());

        } catch (Exception exception) {

        }


    }


    private String getUserDataFullColumnName(String sequenceToFind) {
        List<String> names = this.userDataTable.getRawColumnNames();
        for (String columnName : names) {
            if (columnName.contains(sequenceToFind)) {
                return columnName;
            }
        }
        return null;
    }

}
