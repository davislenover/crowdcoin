package com.crowdcoin.mainBoard.table.tabActions;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.MatchValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.grade.Grade;
import com.crowdcoin.mainBoard.grade.GradeTools;
import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.TabActionEvent;
import com.crowdcoin.mainBoard.window.InfoPopWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.SQLData;
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
                String coinID = selectedRow.get(0).toString();
                // Get all data for the given coinID selected for grading (via the coin table)
                List<Object> coinData = this.mainCoinTable.getSpecificRows(0,coinID,1,0,this.mainCoinTable.getNumberOfColumns()-1).get(0);
                pane.clearAllFields();

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

                InteractiveChoiceBox gradeChoice = new InteractiveChoiceBox("Grade","Select your grading assessment here",(event, field, pane1) -> {

                    if (pane1.getSubmitFieldsSize() == 0) {
                        SubmitField submitButton = new InteractiveButton("Submit Assessment",(event1, button, pane2) -> {
                            InfoPopWindow confirmWindow = new InfoPopWindow("Confirm Grade",manager);
                            confirmWindow.setInfoMessage("Submit Grade of " + pane2.getInputField(pane2.getFieldsSize()-1).getInput() + " for coinID " + pane2.getInputField(0).getInput() + "?");
                            confirmWindow.setOkButtonMessage("Yes");

                            confirmWindow.setOkButtonAction((event2, button1, pane3) -> {

                                // Find the column pertaining to the current user (to write the grade to)
                                int columnWriteIndex = 0;
                                for (String columnName : table.getColumnNames()) {
                                    if (columnName.contains(SQLData.credentials.getUsername())) {
                                        break;
                                    }
                                    columnWriteIndex++;
                                }

                                try {
                                    // Get the grade the user chose
                                    Grade grade = Grade.valueOf(pane2.getInputField(pane2.getFieldsSize()-1).getInput());
                                    // Write the grade to the database
                                    table.writeToRow(columnWriteIndex,String.valueOf(grade.getGradeCode()),0,coinID);

                                    // Check if coin can be given a full grade
                                    GradeTools gradeChecker = new GradeTools(coinID,table);
                                    if (gradeChecker.hasEveryoneGradedID()) {
                                        // Write average grade to the corresponding average grade column
                                        table.writeToRow(1,String.valueOf(gradeChecker.getGradeAverage()),0,coinID);
                                        // Write word grade to main coin table
                                        mainCoinTable.writeToRow(3,gradeChecker.getGrade().toString(),0,coinID);
                                    }

                                    confirmWindow.closeWindow();
                                } catch (Exception e) {
                                    // TODO Error handling
                                    e.printStackTrace();
                                }

                            });

                            SubmitField cancelConfirmation = new InteractiveButton("No",((event2, button1, pane3) -> {
                                confirmWindow.closeWindow();
                            }));
                            cancelConfirmation.setOrder(1);
                            confirmWindow.getWindowPane().addSubmitField(cancelConfirmation);

                            try {
                                confirmWindow.start(StageManager.getStage(confirmWindow));
                            } catch (Exception e) {
                                // TODO Error handling
                            }
                        });

                        pane1.addSubmitField(submitButton);
                        pane1.notifyObservers(new ModifyEvent(ModifyEventType.PANE_UPDATE));
                    }

                });

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
