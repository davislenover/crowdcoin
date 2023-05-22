package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.layout.GridPane;

public interface InputField {

    void applyPane(GridPane targetPane, int targetRow);
    String getInput();

}
