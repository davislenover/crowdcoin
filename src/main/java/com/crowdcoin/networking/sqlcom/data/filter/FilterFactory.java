package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

import java.lang.reflect.InvocationTargetException;

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
     * @param operator the given operator as an enum object
     * @param params all parameters needed to construct the given Filter object
     * @return a corresponding Filter object
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static Filter constructFilter(FilterOperators operator, Object ... params) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(params);
    }

    public static Filter constructBlankFilter(FilterOperators operator) {
        try {
            return (Filter) operator.getOperatorClass().getConstructors()[1].newInstance();
        } catch (Exception e) {
            return null;
        }
    }

}
