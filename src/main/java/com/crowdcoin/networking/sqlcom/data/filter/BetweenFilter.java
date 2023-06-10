package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

public class BetweenFilter implements Filter {
    private ExtendedFilterOperators operator = ExtendedFilterOperators.BETWEEN;
    private String columnName;
    private Object value1;
    private Object value2;

    public BetweenFilter(String columnName, Object value1, Object value2) {
        this.columnName = columnName;
        this.value1 = value1;
        this.value2 = value2;
    }

    @Override
    public String getFilterString() {
        return " WHERE " + this.columnName + " " + operator.getOperatorString() + " " + value1.toString() + " AND " + value2.toString();
    }

    @Override
    public FilterOperators getOperator() {
        return this.operator;
    }


}
