package com.crowdcoin.networking.sqlcom.data.filter.build;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.List;

public class GeneralFilterBuilder implements FilterBuilder {
    @Override
    public Filter buildFilter(FilterOperators operator, String targetColumn, List<Object> buildParams) {
        try {
            // get first value of buildParams as GeneralFilters only require a singular value
            return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(targetColumn,operator,buildParams.get(0));
        } catch (Exception e) {
            return null;
        }
    }
}
