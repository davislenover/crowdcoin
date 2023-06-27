package com.crowdcoin.networking.sqlcom.data.filter.build;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.List;

public class ExtendedFilterBuilder implements FilterBuilder {

    @Override
    public Filter buildFilter(FilterOperators operator, String targetColumn, List<Object> buildParams) {
        try {
            // pass entire buildParams as ExtendedFilters require more than a singular value
            return (Filter) operator.getOperatorClass().getConstructors()[0].newInstance(targetColumn,buildParams);
        } catch (Exception e) {
            return null;
        }
    }
}
