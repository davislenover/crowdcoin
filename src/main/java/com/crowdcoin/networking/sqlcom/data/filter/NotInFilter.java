package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Creates an NOT IN SQL WHERE statement. NOT IN is effectively an OR statement, for each row, it looks within a specified column for certain values and excludes them
 */
public class NotInFilter implements Filter {

    private ExtendedFilterOperators operator = ExtendedFilterOperators.NOTIN;
    private String columnName;
    private List<Object> values;

    /**
     * Creates a Not In filter
     * @param columnName the column name within the SQL database to search for specified values
     * @param values the values to look for inside the column
     * @Note Values will be converted to strings (as specified by the Object super class)
     */
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
