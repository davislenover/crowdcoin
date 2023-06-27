package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class FilterFactory {

    /**
     * Gets the corresponding FilterOperator enum object
     * @param filterName the given filter name as a String object. Note this must match the filter enum name exactly.
     * @return the given FilterOperator enum object, null if not found
     */
    public static FilterOperators getOperatorEnum(String filterName) {

        for (GeneralFilterOperators operator : GeneralFilterOperators.values()) {
            if (operator.toString().equals(filterName)) {
                return operator;
            }
        }

        for (ExtendedFilterOperators operator : ExtendedFilterOperators.values()) {
            if (operator.toString().equals(filterName)) {
                return operator;
            }
        }

        return null;

    }

    /**
     * Constructs a new filter given a specified enum operator
     * @param params all parameters needed to construct the given Filter object. The first should be the column name, second the operator
     * @return a corresponding Filter object (i.e., BETWEEN enum will return a BetweenFilter object). If the operation fails, null is returned
     */
    public static Filter constructFilter(List<String> params) {

        try {

            String columnName = params.get(0);
            FilterOperators operator = FilterFactory.getOperatorEnum(params.get(1));

            if (operator instanceof GeneralFilterOperators) {
                return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(columnName,operator,params.get(2));
            } else if (operator instanceof ExtendedFilterOperators) {
                // Between Filter
                if (params.size() != 3) {
                    return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(columnName,params.get(2),params.get(3));
                }

                // Rest are IN/NOT IN Filters
                // Parse params[3] (TextArea) by newline
                List<String> parsedValues = List.of(params.get(2).split("\n"));
                return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(columnName,parsedValues);

            }


        } catch (Exception e) {
            return null;
        }

        return null;

    }

    /**
     * Construct a blank filter object given a specified enum operator. The blank filter contains no information and should only be used for getting fields for panes/windows
     * @param operator the given operator as an enum object
     * @return a blank Filter object corresponding to the given enum (i.e., BETWEEN enum will return a BetweenFilter object). If the operation fails, null is returned
     */
    public static Filter constructBlankFilter(FilterOperators operator) {
        try {
            return (Filter) operator.getOperatorClass().getConstructors()[1].newInstance();
        } catch (Exception e) {
            return null;
        }
    }

}
