package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.control.Button;

public class ButtonCombo {

    private Button button;
    private ButtonHandler buttonHandler;
    private InteractivePane parentPane;

    public ButtonCombo(String buttonText, ButtonHandler buttonActionHandler, InteractivePane pane) {

        this.button = new Button(buttonText);
        this.buttonHandler = buttonActionHandler;
        this.parentPane = pane;

        this.button.setOnAction(event -> this.buttonHandler.handleButtonClick(event,this.button,this.parentPane));

    }


}
