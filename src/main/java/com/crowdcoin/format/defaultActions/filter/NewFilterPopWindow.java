package com.crowdcoin.format.defaultActions.filter;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.table.Tab;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.data.filter.build.BlankFilterBuilder;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterBuildDirector;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterOperatorTools;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NewFilterPopWindow implements EventHandler {

    private SQLTable table;
    private FilterManager filterManager;
    private SplitMenuButton filterButton;

    private List<String> allOperators = new ArrayList<>() {{
        addAll(GeneralFilterOperators.getNames());
        addAll(ExtendedFilterOperators.getNames());
    }};

    public NewFilterPopWindow(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table) {
        this.filterButton = filterButton;
        this.filterManager = filterManager;
        this.table = table;
    }

    @Override
    public void handle(Event event) {

        // Create a new window and get it's InteractivePane
        PopWindow newWindow = new PopWindow("New Filter");
        InteractivePane newPane = newWindow.getWindowPane();

        // Add target column name field
        InteractiveChoiceBox choiceBoxColumns = new InteractiveChoiceBox("Target column","The column to apply the filter to",new FieldActionDummyEvent());
        choiceBoxColumns.addAllValues(this.table.getColumnNames());
        choiceBoxColumns.addValidator(new LengthValidator(1));
        newPane.addInputField(choiceBoxColumns);

        // Add operation field
        // Operation field requires more logic as arbitrary logic will be to be invoked given specific operator selection
        InteractiveChoiceBox choiceBoxOperation = new InteractiveChoiceBox("Operation","Operation applied to target column to compare values",(action,field,pane) -> {

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
            buildDirector.createFilter(FilterOperatorTools.getEnum(choiceBox.getValue().toString()),null,null).applyInputFieldsOnWindow(newPane,newWindow);
            // applyInput does not update the window (not it's responsibility) thus call update
            newWindow.updateWindow();


        });

        choiceBoxOperation.addAllValues(allOperators);
        choiceBoxOperation.addValidator(new LengthValidator(1));
        newPane.addInputField(choiceBoxOperation);

        newPane.addButton("OK", ((actionEvent, button, pane) -> {

            boolean areFieldsGood = true;

            for (InputField field : newPane) {
                try {
                    field.validateField();
                    field.hideInfo();
                } catch (ValidationException e) {
                    field.getInfoBox().setInfoText(e.getMessage());
                    field.showInfo();
                    areFieldsGood = false;
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

                // Add filter to manager and close window
                filterManager.add(filterToAdd);
                newWindow.closeWindow();

                // Call sql notify method to update
                // This will trigger tab to notify TabBar to "refresh" the Tab
                this.table.notifyObservers();
            }


        }));

        newWindow.setWindowHeight(300);
        newWindow.setWindowWidth(425);

        try {
            newWindow.start(new Stage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
