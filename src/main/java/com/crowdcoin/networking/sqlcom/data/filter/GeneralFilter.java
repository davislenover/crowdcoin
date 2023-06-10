package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

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
    @Override
    public String getFilterString() {
        return " WHERE " + columnName + " " + operator.getOperatorString() + this.value.toString();
    }

    @Override
    public FilterOperators getOperator() {
        return this.operator;
    }
}
