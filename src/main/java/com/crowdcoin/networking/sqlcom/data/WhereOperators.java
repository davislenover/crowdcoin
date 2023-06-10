package com.crowdcoin.networking.sqlcom.data;

import java.util.HashMap;

public enum WhereOperators {
    EQUAL,GREATERTHAN,LESSTHAN,GREATERTHANOREQUAL,LESSTHANOREQUAL,NOTEQUAL,BETWEEN,IN,NOTIN;

    private static HashMap<WhereOperators,String> lookupOperators = new HashMap<>() {{
       put(EQUAL,"=");
       put(GREATERTHAN,">");
       put(LESSTHAN,"<");
       put(GREATERTHANOREQUAL,">=");
       put(LESSTHANOREQUAL,"<=");
       put(NOTEQUAL,"<>");
       put(BETWEEN,"BETWEEN");
       put(IN,"IN");
       put(NOTIN,"NOT IN");
    }};

    public String getOperatorString() {
        return lookupOperators.get(this);
    }

}
