package com.crowdcoin.networking.sqlcom.data.filter.filterOperators;

import com.crowdcoin.mainBoard.Interactive.InputField;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to contain a specific group of SQL operators. Classes that implement this interface should be enum's
 */
public interface FilterOperators {
    /**
     * Gets the operator object as a string (may be different from the name of the object). Typically used to insert the operator into an SQL WHERE statement
     * @return
     */
    String getOperatorString();

    /**
     * Gets the class (not enum) of the given enum operator
     * @return the Class of the given enum operator
     */
    Class getOperatorClass();

}
