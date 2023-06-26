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


        InteractiveFieldActionEvent dummyEvent = new InteractiveFieldActionEvent() {
            @Override
            public void fieldActionHandler(ActionEvent event, Control field, InteractivePane pane) {
                return;
            }
        };

        newFilter.setOnAction(event -> {

            PopWindow newWindow = new PopWindow("New Filter",table);

            InteractiveWindowPane newPane = newWindow.getWindowPane();

            newPane.addChoiceField("Target Column","The column to apply the filter to",dummyEvent,table.getColumnNames().toArray(new String[0]));
            List<String> allOperators = new ArrayList<>() {{
                addAll(GeneralFilterOperators.getNames());
                addAll(ExtendedFilterOperators.getNames());
            }};
            newPane.addChoiceField("Operation", "Operation applied to target column to compare values", (action,field,pane) -> {

                // Reset to two fields
                newPane.retainAllFields(new ArrayList<>() {{
                    add(newPane.getInputField(0));
                    add(newPane.getInputField(1));
                }});

                ChoiceBox choiceBox = (ChoiceBox) field;
                FilterFactory.constructBlankFilter(FilterFactory.getOperatorEnum(choiceBox.getValue().toString())).applyInputFieldsOnWindow(newPane,newWindow);
                newWindow.updateWindow();


            },allOperators.toArray(new String[0]));
            newPane.getInputField(1).setDescWrappingWidth(180);

            newPane.addButton("OK", new InteractiveButtonActionEvent() {
                @Override
                public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
                    return;
                }
            });

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
