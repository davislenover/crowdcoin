package com.crowdcoin.mainBoard.Interactive;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;

public interface ButtonHandler {
    void handleButtonClick(ActionEvent event, Button button, InteractivePane pane);

}
