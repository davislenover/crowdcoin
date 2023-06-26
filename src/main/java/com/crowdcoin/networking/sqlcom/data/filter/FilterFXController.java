package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.InteractiveButtonActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractiveFieldActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveWindowPane;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
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

        // Crude Implementation of PopWindow for Filters (primarily for testing)
        // Will need to give more flexibility to creating windows (i.e., ability to set spacing between text and fields in fieldPane)
        // This may require adding a HBox/VBox to nodes within the same row and inserting the HBoxes into the fieldPane
        newFilter.setOnAction(event -> {

            PopWindow newWindow = new PopWindow("New Filter",table);

            InteractiveWindowPane newPane = newWindow.getWindowPane();

            newPane.addChoiceField("Target Column","The column to apply the filter to",dummyEvent,table.getColumnNames().toArray(new String[0]));
            List<String> allOperators = new ArrayList<>() {{
               addAll(GeneralFilterOperators.getNames());
               addAll(ExtendedFilterOperators.getNames());
            }};
            newPane.addChoiceField("Operation", "Operation applied to target column to compare values", dummyEvent,allOperators.toArray(new String[0]));
            newPane.getInputField(1).setDescWrappingWidth(180);
            newPane.addField("Target value", "The value used alongside the operator",dummyEvent);

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
