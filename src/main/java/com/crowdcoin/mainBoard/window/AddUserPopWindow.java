package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.DoesNotContainValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.InputValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import javafx.stage.Stage;

public class AddUserPopWindow extends PopWindow {

    private static String[] invalidUsernameSequences = {"USERID_","USERID",","};

    public AddUserPopWindow(String windowName) {
        super(windowName);
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();

        InputField username = new InteractiveTextField("Username","The given username for the new account",(event, field, pane1) -> {return;});
        InputField password = new InteractiveTextField("Password","The given starting password for the new account",(event, field, pane1) -> {return;});
        InputField isAdmin = new InteractiveChoiceBox("Is an Admin?","Grants the user elevated privileges",(event, field, pane1) -> {return;},"Yes","No");

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

        SubmitField addUserBtn = new InteractiveButton("Add User",(event, button, pane1) -> {return;});
        SubmitField cancelBtn = new InteractiveButton("Cancel",(event, button, pane1) -> {return;});
        cancelBtn.setOrder(1);

        pane.addSubmitField(addUserBtn);
        pane.addSubmitField(cancelBtn);


    }



}
