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
     * Defines if a column is user readable onscreen. If not, classes can still obtain this column but should not display such on screen
     * @return true if the column is user readable, false otherwise
     */
    boolean isUserReadable() default true;

    /**
     * Defines if a column is user writable. If not, classes can still write to this column but should not provide a users a method of doing so on-screen
     * @return true if the column is user writeable, false otherwise
     */
    boolean isUserWriteable() default true;
}
