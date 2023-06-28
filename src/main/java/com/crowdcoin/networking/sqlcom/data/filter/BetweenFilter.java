package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.input.validation.ComparatorValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.Interactive.input.validation.TypeValidator;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Creates a BETWEEN SQL WHERE statement. BETWEEN specifies to obtain rows with values in a specific column only between two values
 */
public class BetweenFilter implements Filter {
    private ExtendedFilterOperators operator = ExtendedFilterOperators.BETWEEN;
    private String columnName;
    private Object value1;
    private Object value2;

    /**
     * Creates a Between Filter
     * @param columnName the name of the column with the SQL database to look for values in-between of
     * @param values the two values to use in the filter. The first value in the collection is the smaller and the second is the larger, all other values in the collection are ignored.
     * @Note Both values will be converted to strings (as specified by the Object super class)
     */
    public BetweenFilter(String columnName, Collection<Object> values) {
        this.columnName = columnName;

        // Place collection into list for extraction
        List<Object> inputValues = new ArrayList<>() {{
            addAll(values);
        }};

        this.value1 = inputValues.get(0);
        this.value2 = inputValues.get(1);
    }

    /**
     * Creates a blank BetweenFilter object
     */
    public BetweenFilter() {

    }

    @Override
    public String getFilterString() {
        return " WHERE " + this.columnName + " " + operator.getOperatorString() + " " + "'" + value1.toString() + "'" + " AND " + "'" + value2.toString() + "'";
    }

    @Override
    public String getBareString() {
        return getFilterString().substring(6);
    }

    @Override
    public FilterOperators getOperator() {
        return this.operator;
    }

    @Override
    public String getTargetColumnName() {
        return this.columnName;
    }

    @Override
    public void applyInputFieldsOnWindow(InteractivePane targetPane, PopWindow targetWindow) {
        InputField field = new InteractiveTextField("First value","The lower value within the between operator",targetPane,new FieldActionDummyEvent());
        field.addValidator(new LengthValidator(1));
        field.addValidator(new TypeValidator(Integer.class));
        targetPane.addInputField(field);
        InputField field2 = new InteractiveTextField("Second value","The higher value within the between operator",targetPane,new FieldActionDummyEvent());
        field2.addValidator(new LengthValidator(1));
        field2.addValidator(new TypeValidator(Integer.class));
        // Specify comparator with reference to other field to compare values
        field2.addValidator(new ComparatorValidator((input,inputUnused) -> {

            if (Integer.valueOf(input) >= Integer.valueOf(field.getInput())) {
                return 1;
            } else {
                return -1;
            }
                        // Add onto context
                }," first value"));

        targetPane.addInputField(field2);
        targetWindow.setWindowHeight(400);
    }

    @Override
    public boolean equals(Object obj) {

        try {
            Filter filterToTest = (Filter) obj;
            // To test if filter is the same, check if both the operator and SQL query string are the same between Filters
            if (filterToTest.getOperator().equals(this.operator) && filterToTest.getFilterString().equals(this.getFilterString())) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            return false;
        }
    }
}
