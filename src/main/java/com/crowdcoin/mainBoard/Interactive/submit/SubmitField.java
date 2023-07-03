package com.crowdcoin.mainBoard.Interactive.submit;

import com.crowdcoin.mainBoard.Interactive.Field;
import javafx.scene.layout.GridPane;

public interface SubmitField extends Field {

    /**
     * Add object to a GridPane
     * @param targetPane the GridPane object to add the object to. The column at which the SubmitField is applied to shall be determined by the order value
     */
    void applyPane(GridPane targetPane, int targetRow);

}
