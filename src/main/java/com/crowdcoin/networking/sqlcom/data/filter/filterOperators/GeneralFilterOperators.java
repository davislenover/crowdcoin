package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.GeneralFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * GeneralFilterOperators class contains SQL operators from the SQL WHERE statement that only require one value. ExtendedFilterOperators stores special SQL operators that may or may not require more than one value
 */
public enum GeneralFilterOperators implements FilterOperators {
    EQUAL,GREATERTHAN,LESSTHAN,GREATERTHANOREQUAL,LESSTHANOREQUAL,NOTEQUAL;

    private static HashMap<GeneralFilterOperators,String> lookupOperators = new HashMap<>() {{
       put(EQUAL,"=");
       put(GREATERTHAN,">");
       put(LESSTHAN,"<");
       put(GREATERTHANOREQUAL,">=");
       put(LESSTHANOREQUAL,"<=");
       put(NOTEQUAL,"<>");
    }};


    @Override
    public String getOperatorString() {
        return lookupOperators.get(this);
    }

    @Override
    public Class getOperatorClass() {
        return GeneralFilter.class;
    }

    public static List<String> getNames() {

        List<String> returnList = new ArrayList<>();

        for (FilterOperators operator : values()) {
            returnList.add(operator.toString());
        }

        return returnList;
    }

}
