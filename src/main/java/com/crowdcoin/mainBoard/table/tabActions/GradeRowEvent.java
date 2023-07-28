package com.crowdcoin.mainBoard.table.tabActions;

import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.MatchValidator;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.grade.Grade;
import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.TabActionEvent;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GradeRowEvent implements TabActionEvent {
    private SQLTable mainCoinTable;

    private List<String> grades = new ArrayList<>() {{
        for (Grade grade : Grade.values()) {
            add(grade.toString());
        }
    }};

    public GradeRowEvent(SQLTable coinTable) {
        this.mainCoinTable = coinTable;
    }

    @Override
    public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table, WindowManager manager) {

        // Save the selected row
        columnContainer.setSelectedRow();

        try {
            TableView<ModelClass> tableView = columnContainer.iterator().next().getTableView();
            List<Object> selectedRow = columnContainer.getSelectedRow();
            // Check to make sure a row was selected
            if (!selectedRow.isEmpty()) {
                // Get all data for the given coinID selected for grading (via the coin table)
                List<Object> coinData = this.mainCoinTable.getSpecificRows(0,selectedRow.get(0).toString(),1,0,this.mainCoinTable.getNumberOfColumns()-1).get(0);
                pane.clearAllInputFields();

                int columnIndex = 0;
                for (String columnName : this.mainCoinTable.getColumnNames()) {
                    InputField newField = new InteractiveTextField(columnName,"Value for the specified column",(event, field, pane1) -> {return;});
                    String columnData = coinData.get(columnIndex).toString();
                    newField.setValue(columnData);
                    // Make this unchangeable data
                    newField.addValidator(new MatchValidator(columnData));
                    newField.setOrder(columnIndex);
                    pane.addInputField(newField);
                    columnIndex++;
                }

                InteractiveChoiceBox gradeChoice = new InteractiveChoiceBox("Grade","Select your grading assessment here",(event, field, pane1) -> {return;});
                gradeChoice.addAllValues(grades);
                gradeChoice.addValidator(new LengthValidator(1));
                gradeChoice.setOrder(columnIndex);
                pane.addInputField(gradeChoice);

            }

            // Get pane to notify all observers (particularly it's corresponding Tab) that the InteractivePane has changes, thus, update those changes to the screen
            pane.notifyObservers(new ModifyEvent(ModifyEventType.PANE_UPDATE));

            // notify method will re-apply tab to screen, thus unselecting the row, thus reselect row
            tableView.getSelectionModel().select(columnContainer.getCurrentSelectedRelativeIndex());

        } catch (Exception e) {
            e.printStackTrace();
            // TODO Error handling
        }

    }
}
