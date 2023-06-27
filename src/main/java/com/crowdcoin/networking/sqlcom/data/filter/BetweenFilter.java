package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveTextField;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.ArrayList;
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
     * @param value1 the first (smaller) value
     * @param value2 the second (larger) value
     * @Note Both values will be converted to strings (as specified by the Object super class)
     */
    public BetweenFilter(String columnName, Object value1, Object value2) {
        this.columnName = columnName;
        this.value1 = value1;
        this.value2 = value2;
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
        targetPane.addField("First value","The lower value within the between operator",new FieldActionDummyEvent());
        targetPane.getInputField(targetPane.getFieldsSize()-1).getInfoBox().setInfoText("This field cannot be empty!");
        targetPane.addField("Second value","The higher value within the between operator",new FieldActionDummyEvent());
        targetPane.getInputField(targetPane.getFieldsSize()-1).getInfoBox().setInfoText("This field cannot be empty!");
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
