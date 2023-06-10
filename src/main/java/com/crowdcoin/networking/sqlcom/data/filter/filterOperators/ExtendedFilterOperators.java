package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import java.util.HashMap;

public enum ExtendedFilterOperators implements FilterOperators {

    BETWEEN,IN,NOTIN;

    private static HashMap<FilterOperators,String> lookupOperators = new HashMap<>() {{
        put(BETWEEN,"BETWEEN");
        put(IN,"IN");
        put(NOTIN,"NOT IN");
    }};

    @Override
    public String getOperatorString() {
        return lookupOperators.get(this);
    }
}
