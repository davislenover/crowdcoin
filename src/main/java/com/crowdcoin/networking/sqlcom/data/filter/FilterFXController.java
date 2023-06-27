package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.*;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.build.BlankFilterBuilder;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterBuildDirector;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterOperatorTools;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.*;

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
            newPane.getInputField(newPane.getFieldsSize()-1).getInfoBox().setInfoText("This field cannot be empty!");
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
                // Create a new filter build director
                FilterBuildDirector buildDirector = new FilterBuildDirector(new BlankFilterBuilder());
                // Call buildDirector to construct blank Filter and call method within to apply fields needed to pane/window
                System.out.println(FilterOperatorTools.getEnum(choiceBox.getValue().toString()));
                buildDirector.createFilter(FilterOperatorTools.getEnum(choiceBox.getValue().toString()),null,null).applyInputFieldsOnWindow(newPane,newWindow);
                // applyInput does not update the window (not it's responsibility) thus call update
                newWindow.updateWindow();


            },allOperators.toArray(new String[0]));

            newPane.getInputField(newPane.getFieldsSize()-1).getInfoBox().setInfoText("This field cannot be empty!");

            newPane.addButton("OK", ((actionEvent, button, pane) -> {

                boolean areFieldsGood = true;

                for (Iterator<InputField> it = pane.getIterator(); it.hasNext();) {
                    InputField field = it.next();
                    if (field.getInput() == null || field.getInput().isBlank()) {
                        areFieldsGood = false;
                        field.showInfo();
                    } else {
                        field.hideInfo();
                    }
                }

                if (areFieldsGood) {

                    // Parse all input (i.e., organize into ordered list)
                    List<Object> parsedInput = FilterOperatorTools.parseInput(pane.getAllInput());

                    // Get the corresponding operator enum
                    FilterOperators operator = FilterOperatorTools.getEnum((String) parsedInput.get(1));

                    // Create a new filter build director
                    // Get the corresponding builder by calling operator enum
                    FilterBuildDirector buildDirector = new FilterBuildDirector(operator.getOperatorBuilder());

                    // Using input from InteractivePane, call factory to construct corresponding filter
                    Filter filterToAdd = buildDirector.createFilter(operator,(String) parsedInput.get(0), (List<Object>) parsedInput.get(2));
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
