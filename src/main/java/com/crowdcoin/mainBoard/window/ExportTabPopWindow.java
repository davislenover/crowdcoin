package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveDirectoryField;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.export.ExportBehaviour.ExportBehaviour;
import com.crowdcoin.mainBoard.export.ExportBehaviour.ExportBehaviourFactory;
import com.crowdcoin.mainBoard.export.ExportBehaviour.GeneralExportBehaviours;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

public class ExportTabPopWindow extends PopWindow {

    private SQLTable sqlTable;
    private ModelClass modelClass;
    private WindowManager manager;

    public ExportTabPopWindow(String windowName, SQLTable sqlTable, ModelClass modelClass, WindowManager manager) {
        super(windowName);
        this.sqlTable = sqlTable;
        this.modelClass = modelClass;
        this.manager = manager;
        this.manager.addWindow(this);
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();
        InteractiveChoiceBox exportBehaviourOptions = new InteractiveChoiceBox("Export Mode","The mode to use for exporting",(event, field, pane1) -> {

            ChoiceBox choiceBox = (ChoiceBox) field;
            ExportBehaviourFactory factory = new ExportBehaviourFactory(pane1,this,this.sqlTable,this.modelClass);
            ExportBehaviour behaviour = factory.constructExportBehaviour(choiceBox.getValue().toString());
            behaviour.applyInputFieldsOnWindow();

            });

        exportBehaviourOptions.addAllValues(GeneralExportBehaviours.getNames());
        pane.addInputField(exportBehaviourOptions);

        InputField chooseFile = new InteractiveDirectoryField("Directory Path","Choose the directory for the file to be saved to",(event, field, pane1) -> {return;}, StageManager.getStage(this));
        pane.addInputField(chooseFile);

        super.setWindowWidth(460);

        super.start(stage);
    }
}
