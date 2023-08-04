package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import javafx.stage.Stage;

public class AddUserPopWindow extends PopWindow {



    public AddUserPopWindow(String windowName) {
        super(windowName);
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();

        InputField username = new InteractiveTextField("Username","The given username for the new account",(event, field, pane1) -> {return;});
        InputField password = new InteractiveTextField("Password","The given starting password for the new account",(event, field, pane1) -> {return;});
        InputField isAdmin = new InteractiveChoiceBox("Is an Admin?","Grants the user elevated privileges",(event, field, pane1) -> {return;},"Yes","No");

        username.addValidator(new LengthValidator(1));


    }



}
