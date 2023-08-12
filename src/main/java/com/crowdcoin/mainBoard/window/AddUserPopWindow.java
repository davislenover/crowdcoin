package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.DoesNotContainValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.DynamicModelClass;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.data.SQLDatabase;
import com.crowdcoin.networking.sqlcom.data.SQLDatabaseGroup;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class AddUserPopWindow extends PopWindow {

    private static String[] invalidUsernameSequences = {"USERID_","USERID",","};
    private SQLTable table;
    private ModelClass gradingTableModelClass;

    private String[] isAdminOptions = {"Yes","No"};

    public AddUserPopWindow(String windowName, SQLTable table, ModelClass klass) {
        super(windowName);
        this.table = table;
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
                    confirmationWindow.setInfoMessage("Add user " + pane1.getInputField(0).getInput() + "?");
                    SubmitField cancelBtn = new InteractiveButton("No",(event1, button1, pane2) -> {confirmationWindow.closeWindow();});
                    cancelBtn.setOrder(1);
                    confirmationWindow.getWindowPane().addSubmitField(cancelBtn);
                    confirmationWindow.setOkButtonAction((event1, button1, pane2) -> {
                        List<String> paneInput = pane1.getAllInput();

                        try {
                            SQLDatabaseGroup database = this.table.getDatabase().getQueryGroup();
                            String userNameString = paneInput.get(0);
                            String passwordString = paneInput.get(1);
                            boolean isAdminBool = (paneInput.get(2).equals(this.isAdminOptions[0]));
                            database.addNewUser(userNameString,passwordString);
                            database.grantUserPermissions(userNameString,this.table.getConnection().getSchemaName(),SQLPermission.ALTER,SQLPermission.INSERT,SQLPermission.DELETE,SQLPermission.SELECT,SQLPermission.UPDATE);
                            if (isAdminBool) {
                                database.grantGlobalPermissions(userNameString,SQLPermission.GRANT_OPTION,SQLPermission.CREATE_USER,SQLPermission.SHOW_DATABASES);
                            }
                            database.addColumn(this.table.getTableName(),DynamicModelClass.getVariableColumnPrefix(this.gradingTableModelClass)+DynamicModelClass.getNextVariableColumnInteger(this.gradingTableModelClass)+"_"+userNameString+"Value", SQLColumnType.VARCHAR_45,"0");
                            database.executeQueries();
                        } catch (SQLException exception) {
                            exception.printStackTrace();
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
