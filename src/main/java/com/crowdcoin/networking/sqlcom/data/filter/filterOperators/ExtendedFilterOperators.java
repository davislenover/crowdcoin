package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import java.util.HashMap;

/**
 * ExtendedFilterOperators class stores special SQL operators for an SQL WHERE statement that may or may not require more than one value. GeneralFilterOperators class contains SQL operators from the SQL WHERE statement that only require one value.
 */
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
