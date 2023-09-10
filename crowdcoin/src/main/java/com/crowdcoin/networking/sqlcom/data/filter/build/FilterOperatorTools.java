package com.crowdcoin.networking.sqlcom.data.filter.build;


import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.ExtendedFilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;

import java.util.ArrayList;
import java.util.List;

public class FilterOperatorTools {

    /**
     * Gets a enum from the given operator name. The name must match that of an enum
     * @param operatorName the name of the operator corresponding to an enum
     * @return a corresponding FilterOperator object. If the operation fails, null is returned
     */
    public static FilterOperators getEnum(String operatorName) {

        for (FilterOperators operator : GeneralFilterOperators.values()) {
            if (operator.toString().equals(operatorName)) {
                return operator;
            }
        }

        for (FilterOperators operator : ExtendedFilterOperators.values()) {
            if (operator.toString().equals(operatorName)) {
                return operator;
            }
        }

        return null;

    }

    /**
     * Parse input from filter window. This method assumes column name is first, operation is second and the rest is after
     * @param input the input from the window, typically called from getAllInput() in a InteractivePane
     * @return a list of objects, the first two indices should contain Strings whereas the third is a list of Strings
     */
    public static List<Object> parseInput(List<String> input) {

        List<Object> returnList = new ArrayList<>();
        // First, add column name and operator name
        returnList.add(input.get(0));
        returnList.add(input.get(1));

        // The rest are parameters
        List<String> paramsInput = new ArrayList<>();
        for (int index = 2; index < input.size(); index++) {
            // Split on new line in case of IN/NOT IN filters
            List<String> splitString = List.of(input.get(index).split("\n"));
            paramsInput.addAll(splitString);
        }

        returnList.add(paramsInput);

        return returnList;

    }

}
