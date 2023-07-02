package com.crowdcoin.mainBoard.window;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractiveInputPane;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.FilterFXController;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.data.filter.build.BlankFilterBuilder;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterBuildDirector;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterOperatorTools;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NewFilterPopWindow extends PopWindow {

    private SQLTable table;
    private FilterManager filterManager;
    private SplitMenuButton filterButton;
    private FilterFXController filterController;

    private List<String> allOperators = new ArrayList<>() {{
        addAll(GeneralFilterOperators.getNames());
        addAll(ExtendedFilterOperators.getNames());
    }};

    public NewFilterPopWindow(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table, FilterFXController filterController) {
        super("New Filter");
        this.filterButton = filterButton;
        this.filterManager = filterManager;
        this.table = table;
        this.filterController = filterController;
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Get parent window and get it's InteractiveInputPane
        InteractiveInputPane newPane = super.getWindowPane();

        // Add target column name field
        InteractiveChoiceBox choiceBoxColumns = new InteractiveChoiceBox("Target column","The column to apply the filter to",new FieldActionDummyEvent());
        choiceBoxColumns.addAllValues(this.table.getColumnNames());
        choiceBoxColumns.addValidator(new LengthValidator(1));
        newPane.addField(choiceBoxColumns);

        // Add operation field
        // Operation field requires more logic as arbitrary logic will be to be invoked given specific operator selection
        InteractiveChoiceBox choiceBoxOperation = new InteractiveChoiceBox("Operation","Operation applied to target column to compare values",(action,field,pane) -> {

            // Reset to two fields in pane (to remove potentially old fields from previous filter operator selection)
            newPane.retainAllFields(new ArrayList<>() {{
                add(newPane.getField(0));
                add(newPane.getField(1));
            }});

            // Downcast to choice box (to get value)
            ChoiceBox choiceBox = (ChoiceBox) field;
            // Create a new filter build director
            FilterBuildDirector buildDirector = new FilterBuildDirector(new BlankFilterBuilder());
            // Call buildDirector to construct blank Filter and call method within to apply fields needed to pane/window
            buildDirector.createFilter(FilterOperatorTools.getEnum(choiceBox.getValue().toString()),null,null).applyInputFieldsOnWindow(newPane,this);
            // applyInput does not update the window (not it's responsibility) thus call update
            super.updateWindow();


        });

        choiceBoxOperation.addAllValues(allOperators);
        choiceBoxOperation.addValidator(new LengthValidator(1));
        newPane.addField(choiceBoxOperation);

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

                // Using input from InteractiveInputPane, call factory to construct corresponding filter
                Filter filterToAdd = buildDirector.createFilter(operator,(String) parsedInput.get(0), (List<Object>) parsedInput.get(2));

                // Add filter to manager and close window
                filterManager.add(filterToAdd);
                super.closeWindow();

                // Call controller notify method to update
                // This will trigger tab to notify TabBar to "refresh" the Tab
                this.filterController.notifyObservers();
            }


        }));

        super.setWindowHeight(300);
        super.setWindowWidth(425);

        try {
            super.start(stage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
