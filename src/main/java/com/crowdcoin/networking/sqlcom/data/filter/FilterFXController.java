package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.*;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * A class responsible for loading filters into SplitMenuButton objects
 */
public class FilterFXController {

    /**
     * Apply filters to a given SplitMenuButton
     * @param filterButton the target SplitMenuButton
     * @param filterManager the FilterManager object to get the Filter objects from
     */
    public void applyFilters(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table) {
        filterButton.getItems().clear();
        filterButton.getItems().addAll(filterManager.getFilterNodes());

        MenuItem newFilter = new MenuItem();
        newFilter.setText("New Filter...");

        newFilter.setOnAction(event -> {

            // Create a new window and get it's InteractivePane
            PopWindow newWindow = new PopWindow("New Filter",table);
            InteractivePane newPane = newWindow.getWindowPane();

            // Add fields
            newPane.addChoiceField("Target Column","The column to apply the filter to",new FieldActionDummyEvent(),table.getColumnNames().toArray(new String[0]));
            List<String> allOperators = new ArrayList<>() {{
                addAll(GeneralFilterOperators.getNames());
                addAll(ExtendedFilterOperators.getNames());
            }};

            // Operation field requires more logic as arbitrary logic will be to be invoked given specific operator selection
            newPane.addChoiceField("Operation", "Operation applied to target column to compare values", (action,field,pane) -> {

                // Reset to two fields in pane (to remove potentially old fields from previous filter operator selection)
                newPane.retainAllFields(new ArrayList<>() {{
                    add(newPane.getInputField(0));
                    add(newPane.getInputField(1));
                }});

                // Downcast to choice box (to get value)
                ChoiceBox choiceBox = (ChoiceBox) field;
                // Call FilterFactory to construct blank Filter and call method within to apply fields needed to pane/window
                FilterFactory.constructBlankFilter(FilterFactory.getOperatorEnum(choiceBox.getValue().toString())).applyInputFieldsOnWindow(newPane,newWindow);
                // applyInput does not update the window (not it's responsibility) thus call update
                newWindow.updateWindow();


            },allOperators.toArray(new String[0]));

            newPane.addButton("OK", ((actionEvent, button, pane) -> {

                if (!pane.getInputField(0).getInput().isBlank() && !pane.getInputField(1).getInput().isBlank()) {
                    // Using input from InteractivePane, call factory to construct corresponding filter
                    Filter filterToAdd = FilterFactory.constructFilter(pane.getAllInput());
                    System.out.println(filterToAdd.getFilterString());
                    //newWindow.closeWindow();
                }


            }));

            newWindow.setWindowHeight(300);
            newWindow.setWindowWidth(425);

            try {
                newWindow.start(new Stage());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });

        filterButton.getItems().add(newFilter);

    }
}