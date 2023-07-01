package com.crowdcoin.mainBoard.window;

import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
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
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.SplitMenuButton;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditFilterPopWindow extends PopWindow {

    private SQLTable table;
    private FilterManager filterManager;
    private Filter filter;
    private FilterFXController filterController;

    private List<String> allOperators = new ArrayList<>() {{
        addAll(GeneralFilterOperators.getNames());
        addAll(ExtendedFilterOperators.getNames());
    }};

    public EditFilterPopWindow(Filter filter, FilterManager filterManager, SQLTable table, FilterFXController filterController) {
        super("Edit Filter");
        this.filter = filter;
        this.filterManager = filterManager;
        this.table = table;
        this.filterController = filterController;
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
            newPane.retainAllFields(new ArrayList<>() {{
                add(newPane.getInputField(0));
                add(newPane.getInputField(1));
            }});

            // Downcast to choice box (to get value)
            ChoiceBox choiceBox = (ChoiceBox) field;
            // Create a new filter build director
            FilterBuildDirector buildDirector = new FilterBuildDirector(new BlankFilterBuilder());
            // Call buildDirector to construct blank Filter and call method within to apply fields needed to pane/window
            buildDirector.createFilter(FilterOperatorTools.getEnum(choiceBox.getValue().toString()),null,null).applyInputFieldsOnWindow(newPane,this);

            // Populate Fields with values
            // Get values from filter
            List<Object> filterValues = filter.getFilterValues();
            // Start at index 2 as it's known that the first two fields are the column name and the operator, thus all other fields are value fields
            for(int index = 2; index < newPane.getFieldsSize(); index++) {

                InputField valueField = newPane.getInputField(index);
                // Index is aligned in Filter as fields were added in the same order that values were entered into Filters
                valueField.setValue((filterValues.get(index-2).toString()));

            }

            // applyInput does not update the window (not it's responsibility) thus call update
            super.updateWindow();


        });
        choiceBoxOperation.addAllValues(allOperators);
        choiceBoxOperation.addValidator(new LengthValidator(1));
        // Add choiceBoxOperation to InteractivePane before setting value as this will trigger the event and the event takes in the pane. If the pane does not exist when triggered, a null pointer exception will occur
        newPane.addInputField(choiceBoxOperation);

        // Add button
        newPane.addButton("OK",(actionEvent,button,pane) -> {});

        // Start the stage to populate stage variable. This is done such that updateWindow() in the trigger for choiceBoxOperation' event works (as it is needed to be triggered below)
        super.start(stage);

        // Set the value of the ChoiceBoxOperation to the given Filter operator
        // This will trigger the action defined above to populate the given inputs for the field
        choiceBoxOperation.setValue(this.filter.getOperator().toString());

    }
}
