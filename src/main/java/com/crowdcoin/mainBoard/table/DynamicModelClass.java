package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.List;

public class DynamicModelClass extends ModelClass {


    /**
     * Create a ModelClass for TableColumn's to reference (i.e., TableColumns will invoke specified methods which get data within the ModelClass to display on screen).
     * This class supports variable methods which are used for more than one column
     * @param classInstance     class instance which contains associatedMethods
     * @param associatedMethods executable methods from class instance
     * @param columns
     * @Note It is recommended to call ModelClassFactory build() on a class instance to produce a ModelClass object
     */
    public DynamicModelClass(Object classInstance, List<Method> associatedMethods, List<Column> columns) {
        super(classInstance, associatedMethods, columns);
    }

    /**
     * Get data from a specified method found within the class instance. Checks column objects for matching column name then invokes corresponding method specified within the Column object.
     * If a method is marked as {@link TableReadable#isVariable()}, then the columns' ordinal position is passed as a second parameter. This method also does not check for an exact match for a Column name but rather any sequence of chars which match
     * @param columnName the given column name. Must match exactly with a Column.getColumnName() call for any given Column object within the list
     * @return an Object instance containing the return of the specified method. Null if not found (i.e. the column name did not match or an exception occured)
     */
    public Object getData(String columnName) {

        try {
            for (Column column : super.getColumns()) {
                if (column.getColumnName().contains(columnName)) {
                    if (column.getColumnDataMethod().getAnnotation(TableReadable.class).isVariable()) {
                        return column.getColumnDataMethod().invoke(super.getInstance(),column.getOrdinalPosition());
                    }
                    return column.getColumnDataMethod().invoke(super.getInstance());
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;

    }

}
