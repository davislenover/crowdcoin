package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.*;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextArea;
import com.crowdcoin.mainBoard.Interactive.input.validation.LengthValidator;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates an IN SQL WHERE statement. IN is effectively an OR statement, for each row, it looks within a specified column for certain values
 */
public class InFilter implements Filter {

    private ExtendedFilterOperators operator = ExtendedFilterOperators.IN;
    private String columnName;
    private List<Object> values;

    /**
     * Creates an In filter
     * @param columnName the column name within the SQL database to search for specified values
     * @param values the values to look for inside the column. Each value should be in its own index position
     * @Note Values will be converted to strings (as specified by the Object super class)
     */
    public InFilter(String columnName, Collection<Object> values) {
        this.columnName = columnName;
        this.values = new ArrayList<>() {{
           addAll(values);
        }};
    }

    /**
     * Creates a blank InFilter object
     */
    public InFilter() {

    }

    /**
     * Gets a list of all Filter values. Filter values are placed in first index as one String with newlines
     * @return a List of Filter values as Objects
     */
    @Override
    public List<Object> getFilterValues() {
        return new ArrayList<>() {{
            String text = "";
            for(Object value : values) {
                text+=value+"\n";
            }
            add(text);
        }};
    }

    @Override
    public String getFilterString() {
        String filter = " WHERE " + this.columnName + " " + operator.getOperatorString() + " (";
        for (int index = 0; index < values.size(); index++) {
            if (index != (values.size() - 1)){
                filter += "'" + values.get(index).toString() + "'" + ", ";
            } else {
                filter += "'" + values.get(index).toString() + "'" + ")";
            }
        }

        return filter;
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
    public void applyInputFieldsOnWindow(InteractiveInputPane targetPane, PopWindow targetWindow) {
        InteractiveTextArea field = new InteractiveTextArea("Values","All values IN the given column separated by new lines",new FieldActionDummyEvent());
        field.addValidator(new LengthValidator(1));
        targetPane.addField(field);
        targetWindow.setWindowHeight(350);
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
