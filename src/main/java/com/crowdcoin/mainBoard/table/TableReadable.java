package com.crowdcoin.mainBoard.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Specifies methods which are to be used to get data from for a ColumnContainer object
 * @Note order parameter specifies the order in which the methods are placed within the ModelClass object. Default 0.
 */
// Specify this annotation is to be used on methods
@Target(ElementType.METHOD)
// Specifies that annotation can be used a runtime via reflection
@Retention(RetentionPolicy.RUNTIME)
public @interface TableReadable {
    // One can specify the order at which methods are found within the ModelClass list
    /**
     * Specifies the order in which the methods are placed within the ModelClass object. Default 0.
     */
    int order() default 0;

    /**
     * Defines if a column is user readable onscreen. If not, classes can still obtain this column but should not display such on screen. Default true
     * @return true if the column is user readable, false otherwise
     */
    boolean isUserReadable() default true;

    /**
     * Defines if a column is user writable. If not, classes can still write to this column but should not provide a users a method of doing so on-screen. Default true
     * @return true if the column is user writeable, false otherwise
     */
    boolean isUserWriteable() default true;

    /**
     * Defines if a column is system writable. If not, classes can still utilize user input to write to the column (if specified to do so from UserWritable) but classes should not internally write to the column.
     * @return true if the column is system writeable, false otherwise
     */
    boolean isSystemWriteable() default true;

    /**
     * Defines is a given method is responsible for any number of columns. If String is not empty, then the method shall handle calls for any given number of columns that are prefixed with said variableName(). The data that is returned depends on the ID number in the column name right after the prefix.
     * The constructor for this class must also include variable arguments to allow for arbitrary data for an arbitrary number of columns with the same data type
     */
    String variableName() default "";

    /**
     * Specifies the name of the column within the table within the SQL database. Each method MUST specify this
     * @return the name of the column as a String object
     */
    String columnName();

}
