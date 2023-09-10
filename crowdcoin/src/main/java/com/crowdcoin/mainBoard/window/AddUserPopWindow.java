package com.crowdcoin.mainBoard.window;

import com.ratchet.window.StageManager;
import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterCount;
import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterTypeException;
import com.crowdcoin.exceptions.modelClass.MultipleVariableMethodsException;
import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
import com.ratchet.interactive.InteractivePane;
import com.ratchet.interactive.input.InputField;
import com.ratchet.interactive.input.InteractiveChoiceBox;
import com.ratchet.interactive.input.InteractiveTextField;
import com.ratchet.interactive.input.validation.DoesNotContainValidator;
import com.ratchet.interactive.input.validation.InputValidator;
import com.ratchet.interactive.input.validation.LengthValidator;
import com.ratchet.interactive.input.validation.PaneValidator;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.modelClass.DynamicModelClass;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;
import com.crowdcoin.mainBoard.table.modelClass.ModelClassFactory;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.data.SQLDatabaseGroup;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableGroup;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;
import com.ratchet.window.InfoPopWindow;
import com.ratchet.window.PopWindow;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class AddUserPopWindow extends PopWindow {

    private static String[] invalidUsernameSequences = {"USERID_","USERID",","};
    private SQLTable table;
    private SQLTable userGrantsTable;
    private ModelClass gradingTableModelClass;

    private String[] isAdminOptions = {"Yes","No"};

    public AddUserPopWindow(String windowName, SQLTable table, SQLTable userGrantsTable, ModelClass klass) {
        super(windowName);
        this.table = table;
        this.userGrantsTable = userGrantsTable;
        this.gradingTableModelClass = klass;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();

        InputField username = new InteractiveTextField("Username","The given username for the new account",(event, field, pane1) -> {return;});
        InputField password = new InteractiveTextField("Password","The given starting password for the new account",(event, field, pane1) -> {return;});
        InputField isAdmin = new InteractiveChoiceBox("Is an Admin?","Grants the user elevated privileges",(event, field, pane1) -> {return;},this.isAdminOptions[0],this.isAdminOptions[1]);

        InputValidator length = new LengthValidator(1);
        username.addValidator(length);
        username.addValidator(new DoesNotContainValidator(invalidUsernameSequences));
        password.addValidator(new LengthValidator(6));
        isAdmin.addValidator(length);
        password.setOrder(1);
        isAdmin.setOrder(2);
        pane.addInputField(username);
        pane.addInputField(password);
        pane.addInputField(isAdmin);

        SubmitField addUserBtn = new InteractiveButton("Add User",(event, button, pane1) -> {
            if (PaneValidator.isInputValid(pane1)) {
                try {
                    InfoPopWindow confirmationWindow = new InfoPopWindow("Add user confirmation");
                    confirmationWindow.setId("Add user confirmation");
                    confirmationWindow.setInfoMessage("Add user " + pane1.getInputField(0).getInput() + "?");
                    SubmitField cancelBtn = new InteractiveButton("No",(event1, button1, pane2) -> {confirmationWindow.closeWindow();});
                    cancelBtn.setOrder(1);
                    confirmationWindow.getWindowPane().addSubmitField(cancelBtn);
                    confirmationWindow.setOkButtonAction((event1, button1, pane2) -> {
                        List<String> paneInput = pane1.getAllInput();

                        try {
                            // Refresh the table to get the most accurate next userID that is available
                            table.refresh();
                            this.gradingTableModelClass = new ModelClassFactory().buildClone(this.gradingTableModelClass,this.table.getRawRows(0,1,0,this.table.getNumberOfColumns()-1).get(0).toArray());
                            SQLTableGroup grantsGroup = this.userGrantsTable.getQueryGroup();
                            SQLDatabaseGroup database = this.table.getDatabase().getQueryGroup();
                            String userNameString = paneInput.get(0);
                            String passwordString = paneInput.get(1);
                            boolean isAdminBool = (paneInput.get(2).equals(this.isAdminOptions[0]));
                            grantsGroup.writeNewRow(List.of(this.userGrantsTable.getColumnNames().get(0)),List.of(userNameString));
                            database.addNewUser(userNameString,passwordString);
                            database.grantUserPermissions(userNameString,this.table.getConnection().getSchemaName(),SQLPermission.ALTER,SQLPermission.INSERT,SQLPermission.DELETE,SQLPermission.SELECT,SQLPermission.UPDATE);
                            if (isAdminBool) {
                                database.grantGlobalPermissions(userNameString,SQLPermission.GRANT_OPTION,SQLPermission.CREATE_USER,SQLPermission.SHOW_DATABASES);
                            }
                            database.addColumn(this.table.getTableName(),DynamicModelClass.getVariableColumnPrefix(this.gradingTableModelClass)+(DynamicModelClass.getNextVariableColumnInteger(this.gradingTableModelClass)+DynamicModelClass.getStartIndex())+"_"+userNameString+"Value", SQLColumnType.VARCHAR_45,"0");
                            database.executeAllQueries(database,grantsGroup);
                        } catch (SQLException exception) {
                            exception.printStackTrace();
                        } catch (FailedQueryException | UnknownColumnNameException exception) {
                            exception.printStackTrace();
                        } catch (InvalidRangeException e) {
                            throw new RuntimeException(e);
                        } catch (MultipleVariableMethodsException e) {
                            throw new RuntimeException(e);
                        } catch (NotZeroArgumentException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidVariableMethodParameterTypeException e) {
                            throw new RuntimeException(e);
                        } catch (InvalidVariableMethodParameterCount e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        }
                        confirmationWindow.closeWindow();
                        super.closeWindow();
                    });
                    confirmationWindow.start(StageManager.getStage(confirmationWindow));
                } catch (SQLException exception) {
                    exception.printStackTrace();
                    // TODO Error handling
                } catch (Exception exception) {
                    // TODO Error handling
                }

            }



            });
        SubmitField cancelBtn = new InteractiveButton("Cancel",(event, button, pane1) -> {return;});
        cancelBtn.setOrder(1);

        pane.addSubmitField(addUserBtn);
        pane.addSubmitField(cancelBtn);

        super.start(stage);


    }



}
