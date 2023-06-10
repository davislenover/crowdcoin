package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import java.util.HashMap;

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

}
