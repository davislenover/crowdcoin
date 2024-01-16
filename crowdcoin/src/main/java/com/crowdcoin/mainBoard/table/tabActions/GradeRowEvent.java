package com.crowdcoin.mainBoard.table.tabActions;

import com.ratchet.window.StageManager;
import com.ratchet.exceptions.handler.ExceptionGuardian;
import com.ratchet.exceptions.handler.GeneralExceptionHandler;
import com.crowdcoin.exceptions.handler.SQLExceptionHandler;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.ratchet.interactive.InteractiveTabPane;
import com.ratchet.interactive.input.InputField;
import com.ratchet.interactive.input.InteractiveChoiceBox;
import com.ratchet.interactive.input.InteractiveTextField;
import com.ratchet.interactive.input.validation.LengthValidator;
import com.ratchet.interactive.input.validation.MatchValidator;
import com.ratchet.interactive.submit.InteractiveButton;
import com.ratchet.interactive.submit.SubmitField;
import com.ratchet.window.WindowManager;
import com.crowdcoin.mainBoard.grade.Grade;
import com.crowdcoin.mainBoard.grade.GradeTools;
import com.crowdcoin.mainBoard.table.ColumnContainer;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;
import com.ratchet.observe.ModifyEvent;
import com.ratchet.observe.ModifyEventType;
import com.ratchet.window.InfoPopWindow;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableGroup;
import javafx.scene.control.TableView;

import java.sql.SQLException;
import java.util.ArrayList;
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

                InteractiveChoiceBox gradeChoice = new InteractiveChoiceBox("Grade","SelectQuery your grading assessment here",(event, field, pane1) -> {

                    if (pane1.getSubmitFieldsSize() == 0) {
                        SubmitField submitButton = new InteractiveButton("Submit Assessment",(event1, button, pane2) -> {
                            InfoPopWindow confirmWindow = new InfoPopWindow("Confirm Grade",manager);
                            confirmWindow.setInfoMessage("Submit Grade of " + pane2.getInputField(pane2.getFieldsSize()-1).getInput() + " for coinID " + pane2.getInputField(0).getInput() + "?");
                            confirmWindow.setOkButtonMessage("Yes");

                            confirmWindow.setOkButtonAction((event2, button1, pane3) -> {

                                // Find the column pertaining to the current user (to write the grade to)
                                int columnWriteIndex = 0;
                                for (String columnName : table.getRawColumnNames()) {
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

                                        SQLTableGroup gradeGroup = table.getQueryGroup();
                                        SQLTableGroup mainCoinTableGroup = mainCoinTable.getQueryGroup();

                                        // Write average grade to the corresponding average grade column
                                        gradeGroup.writeToRow(1,String.valueOf(gradeChecker.getGradeAverage()),0,coinID);
                                        // Write word grade to main coin table
                                        mainCoinTableGroup.writeToRow(3,gradeChecker.getGrade().toString(),0,coinID);

                                        gradeGroup.executeAllQueries(gradeGroup,mainCoinTableGroup);

                                    }

                                    confirmWindow.closeWindow();
                                } catch (Exception e) {
                                    // TODO Error handling
                                    ExceptionGuardian<Exception> guardian = new ExceptionGuardian<>(new GeneralExceptionHandler());
                                    guardian.handleException(e);
                                }

                            });

                            SubmitField cancelConfirmation = new InteractiveButton("No",((event2, button1, pane3) -> {
                                confirmWindow.closeWindow();
                            }));
                            cancelConfirmation.setOrder(1);
                            confirmWindow.getWindowPane().addSubmitField(cancelConfirmation);
                            confirmWindow.setId("Confirm Grade");

                            try {
                                confirmWindow.start(StageManager.getStage(confirmWindow));
                            } catch (Exception e) {
                                // TODO Error handling
                                ExceptionGuardian<Exception> guardian = new ExceptionGuardian<>(new GeneralExceptionHandler());
                                guardian.handleException(e);
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

        } catch (InvalidRangeException e) {
            ExceptionGuardian<SQLException> guardian = new ExceptionGuardian<>(new SQLExceptionHandler());
            SQLException exception = new SQLException("Empty Query Result","01000",5000);
            guardian.handleException(exception);
            // TODO Error handling
        } catch (IndexOutOfBoundsException e) {
            ExceptionGuardian<SQLException> guardian = new ExceptionGuardian<>(new SQLExceptionHandler());
            SQLException exception = new SQLException("Empty Query Result","01000",5000);
            guardian.handleException(exception);
        } catch (Exception e) {
            ExceptionGuardian<Exception> guardian = new ExceptionGuardian<>(new GeneralExceptionHandler());
            guardian.handleException(e);
        }

    }
}
