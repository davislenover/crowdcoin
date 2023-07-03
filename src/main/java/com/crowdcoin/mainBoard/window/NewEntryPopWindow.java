package com.crowdcoin.mainBoard.window;

import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.table.permissions.IsWriteable;
import com.crowdcoin.mainBoard.table.permissions.Permission;
import javafx.stage.Stage;

public class NewEntryPopWindow extends PopWindow {

    private ModelClass columnData;
    private static String isWriteablePermission = IsWriteable.class.getSimpleName();

    public NewEntryPopWindow(String windowName, ModelClass columnData) {
        super(windowName);
        this.columnData = columnData;
    }

    @Override
    public void start(Stage stage) throws Exception {

        InteractivePane windowPane = super.getWindowPane();

        for (Column column : columnData.getColumns()) {
            if (column.checkPermissionValue(isWriteablePermission)) {
                InputField newField = new InteractiveTextField(column.getColumnName(),"Enter data for the given column",new FieldActionDummyEvent());
                newField.setDescWrappingWidth(200);
                newField.setHeaderWrappingWidth(200);
                newField.setHeaderDescVerticalSpacing(10);
                windowPane.addInputField(newField);
            }
        }

        SubmitField addEntry = new InteractiveButton("Add Entry",((event, button, pane) -> {return;}));
        SubmitField cancelAddEntry = new InteractiveButton("Cancel",((event, button, pane) -> {return;}));
        cancelAddEntry.setOrder(1);
        windowPane.addSubmitField(addEntry);
        windowPane.addSubmitField(cancelAddEntry);

        super.setWindowHeight(600);

        super.start(stage);
    }
}
