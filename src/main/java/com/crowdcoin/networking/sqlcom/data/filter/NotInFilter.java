package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NotInFilter implements Filter {

    private ExtendedFilterOperators operator = ExtendedFilterOperators.NOTIN;
    private String columnName;
    private List<Object> values;

    public NotInFilter(String columnName, Collection<Object> values) {
        this.columnName = columnName;
        this.values = new ArrayList<>() {{
            addAll(values);
        }};
    }

    @Override
    public String getFilterString() {
        String filter = " WHERE " + this.columnName + " " + operator.getOperatorString() + " (";
        for (int index = 0; index < values.size(); index++) {
            if (index != (values.size() - 1)){
                filter += values.get(index).toString() + ", ";
            } else {
                filter += values.get(index).toString() + ")";
            }
        }

        return filter;
    }

    @Override
    public FilterOperators getOperator() {
        return this.operator;
    }

}
