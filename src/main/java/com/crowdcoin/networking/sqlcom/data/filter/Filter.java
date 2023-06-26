package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.mainBoard.Interactive.InputField;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.FilterOperators;

import java.util.Collection;
import java.util.List;

/**
 * A filter class to construct a specific SQL WHERE statement with a specific SQL operator
 */
public interface Filter {

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
     * Gets the input fields needed as InputField objects for a given InteractivePane object, typically for the filter creation window.
     * @return InputField objects needed in a list
     */
    List<InputField> getInputFieldsForPane(InteractivePane targetPane);

}
