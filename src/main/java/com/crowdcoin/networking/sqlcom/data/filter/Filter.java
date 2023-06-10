package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

/**
 * A filter class to construct a specific SQL WHERE statement with a specific SQL operator
 */
public interface Filter {

    /**
     * Gets the filter as an SQL WHERE statement as a String object
     * @return a String object containing the SQL WHERE statement
     */
    String getFilterString();

    /**
     * Gets the operator of the filter as a FilterOperators object
     * @return the filter operator as a FilterOperators object. These are operators found as operators for the SQL WHERE statement
     */
    FilterOperators getOperator();

}
