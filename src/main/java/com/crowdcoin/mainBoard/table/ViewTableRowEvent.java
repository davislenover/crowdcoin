package com.crowdcoin.mainBoard.table;

import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.output.OutputField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.Observe.EventType;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.List;

public class ViewTableRowEvent implements TabActionEvent {

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane) {

        try {

            // Get selected row from TableView
            List<Object> selectedRow = columnContainer.getSelectedRow();
            // Save row index to re-application later
            int selectedRowIndex = columnContainer.iterator().next().getTableView().getSelectionModel().getSelectedIndex();
            // Get ModelClass to get column names (for headers in InputField)
            ObservableList<TableColumn<ModelClass,?>> columns = columnContainer.iterator().next().getTableView().getColumns();

            // Get fields from table, create InputFields for them
            List<InputField> fieldsToAdd = new ArrayList<>();
            for (int index = 0; index < selectedRow.size(); index++) {

                InputField newField = new InteractiveTextField(columns.get(index).getText(),"Value for the specified column",(event, field, pane1) -> {return;});
                newField.setValue(selectedRow.get(index).toString());
                newField.setOrder(index);
                fieldsToAdd.add(newField);

            }

            // Clear all already present fields and add new ones
            pane.clearAllInputFields();
            pane.addAllInputFields(fieldsToAdd);

            // Add Edit/Remove buttons
            SubmitField editRowButton = new InteractiveButton("Submit edits to entry",(event, button, pane1) -> {return;});
            SubmitField removeRowButton = new InteractiveButton("Remove entry",(event, button, pane1) -> {return;});
            removeRowButton.setOrder(1);

            pane.clearAllSubmitFields();
            pane.addSubmitField(editRowButton);
            pane.addSubmitField(removeRowButton);

            // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has been changes, thus, update those changes to the screen
            pane.notifyObservers(new ModifyEvent(EventType.PANE_UPDATE));

            // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
            columnContainer.iterator().next().getTableView().getSelectionModel().select(selectedRowIndex);


        } catch (Exception e) {

        }

    }
}
