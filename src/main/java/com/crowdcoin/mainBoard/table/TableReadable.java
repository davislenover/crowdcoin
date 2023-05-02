package com.crowdcoin.mainBoard.table;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// Specify this annotation is to be used on methods
@Target(ElementType.METHOD)
// Specifies that annotation can be used a runtime via reflection
@Retention(RetentionPolicy.RUNTIME)
public @interface TableReadable {
    // One can specify the order at which methods are found within the ModelClass list
    int order();
}
