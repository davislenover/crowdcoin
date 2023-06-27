package com.crowdcoin.networking.sqlcom.data.filter.build;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.List;

public class FilterBuildDirector {

    private FilterBuilder builder;

    public FilterBuildDirector(FilterBuilder builder) {
        this.builder = builder;
    }

    public void changeBuilder(FilterBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construct a new Filter by utilizing the set builder
     * @param operator the given operator (as an enum object) to create the corresponding filter.
     * @param columnName the name of the target column for the filter to apply to
     * @param buildParams a list of parameters to instantiate the filter
     * @return
     */
    public Filter createFilter(FilterOperators operator, String columnName, List<Object> buildParams) {
        return this.builder.buildFilter(operator,columnName,buildParams);
    }

}
