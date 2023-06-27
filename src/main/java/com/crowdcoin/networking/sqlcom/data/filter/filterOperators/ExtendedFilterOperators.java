package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import com.crowdcoin.networking.sqlcom.data.filter.BetweenFilter;
import com.crowdcoin.networking.sqlcom.data.filter.InFilter;
import com.crowdcoin.networking.sqlcom.data.filter.NotInFilter;
import com.crowdcoin.networking.sqlcom.data.filter.build.ExtendedFilterBuilder;
import com.crowdcoin.networking.sqlcom.data.filter.build.FilterBuilder;

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

    private static HashMap<FilterOperators,Class> lookupOperatorsClass = new HashMap<>() {{
        put(BETWEEN,BetweenFilter.class);
        put(IN,InFilter.class);
        put(NOTIN,NotInFilter.class);
    }};

    @Override
    public String getOperatorString() {
        return lookupOperators.get(this);
    }

    @Override
    public Class getOperatorClass() {
        return lookupOperatorsClass.get(this);
    }

    @Override
    public FilterBuilder getOperatorBuilder() {
        return new ExtendedFilterBuilder();
    }


    public static List<String> getNames() {

        List<String> returnList = new ArrayList<>();

        for (FilterOperators operator : values()) {
            returnList.add(operator.toString());
        }

        return returnList;
    }

}
