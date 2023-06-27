package com.crowdcoin.networking.sqlcom.data.filter.build;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

import java.util.List;

public interface FilterBuilder {

    /**
     * Creates a filter object given the specified operator
     * @param operator the given operator
     * @param targetColumn the target column for the filter to apply to
     * @param buildParams the parameters used to the new given Filter object. This is typically handled by the build director
     * @return a Filter object
     */
    Filter buildFilter(FilterOperators operator, String targetColumn, List<Object> buildParams);

}
