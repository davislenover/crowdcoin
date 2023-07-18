package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveDirectoryField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.export.CSVExporter;
import com.crowdcoin.mainBoard.export.ExportBehaviour.ExportBehaviour;
import com.crowdcoin.mainBoard.export.ExportBehaviour.ExportBehaviourFactory;
import com.crowdcoin.mainBoard.export.ExportBehaviour.GeneralExportBehaviours;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableReader;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ExportTabPopWindow extends PopWindow {

    private SQLTable sqlTable;
    private SQLTableReader sqlTableReader;
    private WindowManager manager;
    private ExportBehaviour exportBehaviour;

    public ExportTabPopWindow(String windowName, SQLTable sqlTable, SQLTableReader sqlTableReader, WindowManager manager) {
        super(windowName);
        this.sqlTable = sqlTable;
        this.sqlTableReader = sqlTableReader;
        this.manager = manager;
        this.manager.addWindow(this);
        super.setWindowHeight(500);
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane pane = super.getWindowPane();
        InteractiveChoiceBox exportBehaviourOptions = new InteractiveChoiceBox("Export Mode","The mode to use for exporting",(event, field, pane1) -> {
            pane1.retainAllInputFields(new ArrayList<>() {{
                add(pane1.getInputField(0));
                add(pane1.getInputField(1));
                add(pane1.getInputField(2));
            }});
            ChoiceBox choiceBox = (ChoiceBox) field;
            ExportBehaviourFactory factory = new ExportBehaviourFactory(pane1,this,this.sqlTableReader);
            ExportBehaviour behaviour = factory.constructExportBehaviour(choiceBox.getValue().toString());
            behaviour.applyInputFieldsOnWindow();
            this.exportBehaviour = behaviour;
            super.updateWindow();

            });

        exportBehaviourOptions.addAllValues(GeneralExportBehaviours.getNames());
        exportBehaviourOptions.addValidator(new LengthValidator(1));
        pane.addInputField(exportBehaviourOptions);

        InputField chooseFile = new InteractiveDirectoryField("Directory Path","Choose the directory for the file to be saved to",(event, field, pane1) -> {return;}, StageManager.getStage(this));
        chooseFile.addValidator(new LengthValidator(1));
        pane.addInputField(chooseFile);

        InputField field = new InteractiveTextField("Filename","The name to be given to the exported file",(event, field1, pane1) -> {return;});
        field.addValidator(new LengthValidator(1));
        pane.addInputField(field);

        SubmitField exportButton = new InteractiveButton("Export",(event, button, pane1) -> {

            if (PaneValidator.isInputValid(pane1)) {

                CSVExporter exporter = new CSVExporter(chooseFile.getInput().split(","));

                try {
                    exporter.writeToFile(this.exportBehaviour.getColumns(),this.exportBehaviour.getEntries(this.getAdditionalParams()),pane1.getInputField(2).getInput());
                    super.closeWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                    // TODO Error handling
                }

            }

        });

        pane.addSubmitField(exportButton);
        super.setWindowWidth(460);
        super.start(stage);
    }

    // Method to get all input for getEntries() params
    private Object[] getAdditionalParams() {

        InteractivePane pane = super.getWindowPane();

        // The main InputFields are Mode, File Path and Filename (3 objects) thus, the rest are for input arguments to an ExportBehaviour object
        Object[] returnParams = new String[pane.getFieldsSize() - 3];

        // Start at index 3 (as the last index in pane, 2, would be filename) and get all other input
        for (int index = 3; index < pane.getFieldsSize(); index++) {
            returnParams[index - 3] = pane.getInputField(index).getInput();
        }

        return returnParams;
    }
}
