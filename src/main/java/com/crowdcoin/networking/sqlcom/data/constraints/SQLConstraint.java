package com.crowdcoin.networking.sqlcom.data.constraints;

import java.util.Comparator;

/**
 * Constraints used to omit columns and rows from TableView. Used internally by the program and not meant for "filter" use by the user
 */
public interface SQLConstraint extends Comparator<SQLConstraint> {

    /**
     * Checks if a given String of data is valid
     * @param data the given data
     * @return true if valid, false otherwise
     */
    boolean isValid(String data);

    /**
     * Sets the order of the constraint. Used for specifying position in a {@link SQLConstraintGroup}
     * @param order the given order (the lower the value, the higher the priority, base of 0) as an Integer
     * @return
     */
    void setOrder(int order);

    /**
     * Gets the order of a constraint
     * @return the given constraints order as an integer
     */
    int getOrder();

    /**
     * Compares SQLConstraints by their order Integers
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second
     */
    default int compare(SQLConstraint o1, SQLConstraint o2) {
        return Integer.compare(o1.getOrder(), o2.getOrder());
    }

}
