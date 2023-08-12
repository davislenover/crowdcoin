package com.crowdcoin.mainBoard.window;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.WindowManager;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.FilterController;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.data.filter.build.BlankFilterBuilder;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterBuildDirector;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterOperatorTools;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EditFilterPopWindow extends PopWindow {

    private SQLTable table;
    private FilterManager filterManager;
    private Filter filter;
    private FilterController filterController;

    private WindowManager windowManager;

    private List<String> allOperators = new ArrayList<>() {{
        addAll(GeneralFilterOperators.getNames());
        addAll(ExtendedFilterOperators.getNames());
    }};

    public EditFilterPopWindow(Filter filter, FilterManager filterManager, SQLTable table, FilterController filterController, WindowManager windowManager) {
        super("Edit Filter");
        this.filter = filter;
        this.filterManager = filterManager;
        this.table = table;
        this.filterController = filterController;
        this.windowManager = windowManager;
        windowManager.addWindow(this);
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Get parent window and get it's InteractivePane
        InteractivePane newPane = super.getWindowPane();

        // Add target column name field
        InteractiveChoiceBox choiceBoxColumns = new InteractiveChoiceBox("Target column","The column to apply the filter to",new FieldActionDummyEvent());
        List<String> columnNames = this.table.getColumnNames();
        choiceBoxColumns.addAllValues(columnNames);
        // Pre-select target name in ChoiceBox
        choiceBoxColumns.setValue(this.filter.getTargetColumnName());
        choiceBoxColumns.addValidator(new LengthValidator(1));
        newPane.addInputField(choiceBoxColumns);

        // Add operation field
        // Operation field requires more logic as arbitrary logic will be to be invoked given specific operator selection
        InteractiveChoiceBox choiceBoxOperation = new InteractiveChoiceBox("Operation","Operation applied to target column to compare values",(action,field,pane) -> {

            // Reset to two fields in pane (to remove potentially old fields from previous filter operator selection)
            newPane.retainAllInputFields(new ArrayList<>() {{
                add(newPane.getInputField(0));
                add(newPane.getInputField(1));
            }});

            // Downcast to choice box (to get value)
            ChoiceBox choiceBox = (ChoiceBox) field;
            // Create a new filter build director
            FilterBuildDirector buildDirector = new FilterBuildDirector(new BlankFilterBuilder());
            // Call buildDirector to construct blank Filter and call method within to apply fields needed to pane/window
            FilterOperators operator = FilterOperatorTools.getEnum(choiceBox.getValue().toString());
            Filter newFilter = buildDirector.createFilter(operator,null,null);
            newFilter.applyInputFieldsOnWindow(newPane,this);

            // Check if the set filter selected in the ChoiceBox matches that of the original filter type
            // Prevents exceptions if the user changes the operator when editing the filter (as the filter values may not match up)
            // This also has the effect that if a user switches the filter operator and then back in the window, the fields are remembered from the original filter operator
            if (filter.getOperator().equals(operator)) {
                // Populate Fields with values from original filter (as it's being edited)
                // Get values from filter
                List<Object> filterValues = filter.getFilterValues();
                // Start at index 2 as it's known that the first two fields are the column name and the operator, thus all other fields are value fields
                for(int index = 2; index < newPane.getFieldsSize(); index++) {

                    InputField valueField = newPane.getInputField(index);
                    // Index is aligned in Filter as fields were added in the same order that values were entered into Filters
                    valueField.setValue((filterValues.get(index-2).toString()));

                }
            }

            // applyInput does not update the window (not it's responsibility) thus call update
            super.updateWindow();


        });
        choiceBoxOperation.addAllValues(allOperators);
        choiceBoxOperation.addValidator(new LengthValidator(1));
        // Add choiceBoxOperation to InteractivePane before setting value as this will trigger the event and the event takes in the pane. If the pane does not exist when triggered, a null pointer exception will occur
        newPane.addInputField(choiceBoxOperation);

        // Add Ok submitField for editing filters
        SubmitField submitFilterEdit = new InteractiveButton("OK",(actionEvent, button, pane) -> {

            // Perform basically the same actions as NewFilterPopWindowClass

            if (PaneValidator.isInputValid(newPane)) {
                // Remove current filter
                // It's much easier to just create a new filter rather than performing a bunch of checks to determine if a new filter is needed
                filterManager.remove(filter);

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
                super.closeWindow();

                // Call controller notify method to update
                // This will trigger tab to notify TabBar to "refresh" the Tab
                ModifyEvent filterEvent = new ModifyEvent(ModifyEventType.NEW_FILTER);
                this.filterController.notifyObservers(filterEvent);
            }
        });

        // Set the order of submitFilterEdit to appear on the very left of the window
        submitFilterEdit.setOrder(0);

        // Add submitFiled to pane
        newPane.addSubmitField(submitFilterEdit);

        // Create remove filter field
        SubmitField removeFilter = new InteractiveButton("Remove Filter",((event, button, pane) -> {

            InfoPopWindow newInfoWindow = new InfoPopWindow("Confirmation",windowManager);
            newInfoWindow.setId("Confirmation");
            newInfoWindow.setInfoMessage("Are you sure you want to remove this filter?");
            newInfoWindow.setOkButtonAction(((event1, button1, pane1) -> {
                // If ok button is pressed, remove the filter and notify controller for refresh to Tab
                filterManager.remove(filter);
                // Close both the Info and EditFilter window (as Filter no longer exists)
                newInfoWindow.closeWindow();
                super.closeWindow();
                ModifyEvent filterEvent = new ModifyEvent(ModifyEventType.NEW_FILTER);
                this.filterController.notifyObservers(filterEvent);
            }));

            InteractivePane infoWindowPane = newInfoWindow.getWindowPane();
            // Create cancel button
            SubmitField cancelField = new InteractiveButton("Cancel",((event1, button1, pane1) -> {
                // Just close the window, nothing else
                newInfoWindow.closeWindow();
            }));

            cancelField.setOrder(1);

            // Add cancel button to infoWindowPane
            infoWindowPane.addSubmitField(cancelField);

            try {
                newInfoWindow.start(StageManager.getStage(newInfoWindow));
            } catch (Exception e) {

            }

        }));

        // Set order to come after ok field
        removeFilter.setOrder(1);

        // Add remove filter field to InteractivePane
        newPane.addSubmitField(removeFilter);


        // Start the stage to populate stage variable. This is done such that updateWindow() in the trigger for choiceBoxOperation' event works (as it is needed to be triggered below)
        super.start(stage);

        // Set the value of the ChoiceBoxOperation to the given Filter operator
        // This will trigger the action defined above to populate the given inputs for the field
        choiceBoxOperation.setValue(this.filter.getOperator().toString());
    }
}
