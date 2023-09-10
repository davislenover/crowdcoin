package com.crowdcoin.networking.sqlcom.data.filter;

import com.ratchet.interactive.InteractivePane;
import com.ratchet.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.List;

/**
 * A filter class to construct a specific SQL WHERE statement with a specific SQL operator
 */
public interface Filter {

    /**
     * Gets a list of all values used by the operator in the Filter. List indices should preserve the order at which the values were passed into the Filter. List should not be a direct reference to method of storage of values within the Filter.
     */
    List<Object> getFilterValues();

    /**
     * Gets the filter as an SQL WHERE statement as a String object
     * @return a String object containing the SQL WHERE statement
     */
    String getFilterString();

    /**
     * Gets the filter as an SQL WHERE statement WITHOUT THE INCLUSION OF "WHERE" as a String object. This is intended to be used to combined multiple filters together.
     * The String returned should start with a blank (space) character
     * @return a String object containing the SQL statement
     */
    String getBareString();

    /**
     * Gets the operator of the filter as a FilterOperators object
     * @return the filter operator as a FilterOperators object. These are operators found as operators for the SQL WHERE statement
     */
    FilterOperators getOperator();

    /**
     * Gets the target column name within the SQL database that the filter will apply to
     * @return the column name as a String object
     */
    String getTargetColumnName();

    /**
     * Apply the input fields needed for a given InteractivePane object, set any Window preferences.
     * This method assumes the given InteractivePane does not already contain the specified InputFields within the method. This method will NOT call any update to prompt a visual change to the window.
     */
    void applyInputFieldsOnWindow(InteractivePane targetPane, PopWindow targetWindow);

}
