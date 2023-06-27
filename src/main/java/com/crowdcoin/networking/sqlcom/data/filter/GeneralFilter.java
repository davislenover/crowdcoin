package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.InteractiveTextField;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A general purpose filter to construct an SQL WHERE statement using GeneralFilterOperator objects
 */
public class GeneralFilter implements Filter {
    private GeneralFilterOperators operator;
    private String columnName;
    private Object value;

    /**
     * Creates a General filter
     * @param columnName the column name within the SQL database to operate in
     * @param operator the general operator
     * @param value the specified value for the operator to utilize
     * @Note Value will be converted to a String (as specified by the Object super class)
     */
    public GeneralFilter(String columnName, GeneralFilterOperators operator, Object value) {
        this.columnName = columnName;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Creates a blank GeneralFilter object
     */
    public GeneralFilter() {

    }

    @Override
    public String getFilterString() {
        return " WHERE " + columnName + " " + operator.getOperatorString() + " " + "'" + this.value.toString() + "'";
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
        targetPane.addField("Value","The value used in conjunction with the operator",new FieldActionDummyEvent());
        targetPane.getInputField(targetPane.getFieldsSize()-1).getInfoBox().setInfoText("This field cannot be empty!");
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
