package com.crowdcoin.networking.sqlcom.data.filter.build;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.List;

/**
 * Construct blank filter objects given a specified enum operator. The blank filter contains no information and should only be used for getting fields for panes/windows
 */
public class BlankFilterBuilder implements FilterBuilder {
    @Override
    public Filter buildFilter(FilterOperators operator, String targetColumn, List<Object> buildParams) {
        try {
            return (Filter) operator.getOperatorClass().getConstructors()[1].newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
