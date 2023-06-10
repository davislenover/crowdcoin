package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import java.util.HashMap;

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
