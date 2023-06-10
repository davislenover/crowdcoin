package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

public interface Filter {
    String getFilterString();
    FilterOperators getOperator();

}
