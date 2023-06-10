package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

public class GeneralFilter implements Filter {
    private GeneralFilterOperators operator;
    private String columnName;
    private Object value;

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
