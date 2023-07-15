package com.crowdcoin.mainBoard.window;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.export.ExportBehaviour.ExportBehaviour;
import com.crowdcoin.mainBoard.export.ExportBehaviour.GeneralExportBehaviours;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.Arrays;

public class ExportTabPopWindow extends PopWindow {

    private SQLTable sqlTable;
    private ModelClass modelClass;

    public ExportTabPopWindow(String windowName, SQLTable sqlTable, ModelClass modelClass) {
        super(windowName);
        this.sqlTable = sqlTable;
        this.modelClass = modelClass;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();
        InputField exportBehaviourOptions = new InteractiveChoiceBox("Export Mode","The mode to use for exporting",(event, field, pane1) -> {

            ChoiceBox choiceBox = (ChoiceBox) field;
            try {
                ExportBehaviour behaviour = (ExportBehaviour) GeneralExportBehaviours.valueOf(choiceBox.getValue().toString()).getExportBehaviourClass().getConstructors()[0].newInstance(pane1,this,this.sqlTable,this.modelClass);
                behaviour.applyInputFieldsOnWindow();
            } catch (Exception e) {
                // TODO Error handling
            }
            }, Arrays.toString(GeneralExportBehaviours.values()));
        pane.addInputField(exportBehaviourOptions);

        super.start(stage);
    }
}
