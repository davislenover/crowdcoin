package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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


    public static List<String> getNames() {

        List<String> returnList = new ArrayList<>();

        for (FilterOperators operator : values()) {
            returnList.add(operator.getOperatorString());
        }

        return returnList;
    }

}
