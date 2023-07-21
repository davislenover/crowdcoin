package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ModelClass {

    private Object classWithMethods;
    private List<Method> dataMethods;
    private List<Column> columns;

    /**
     * Create a ModelClass for TableColumn's to reference (i.e., TableColumns will invoke specified methods which get data within the ModelClass to display on screen)
     * @param classInstance class instance which contains associatedMethods
     * @param associatedMethods executable methods from class instance
     * @Note It is recommended to call ModelClassFactory build() on a class instance to produce a ModelClass object
     */
    public ModelClass(Object classInstance, List<Method> associatedMethods, List<Column> columns) {
        this.classWithMethods = classInstance;
        this.dataMethods = associatedMethods;
        this.columns = columns;
    }

    /**
     * Get data from a specified method found within the class instance. This method does NOT take into account dynamic methods within a ModelClass
     * @param methodIndex the index at which the method is stored within the ModelClass instance
     * @return an Object instance containing the return of the specified method
     * @throws IndexOutOfBoundsException if the method it not within the ModelClass instance
     */
    public Object getData(int methodIndex) {

        try {
            // Invoke specified method in class and return result as object
            return this.dataMethods.get(methodIndex).invoke(this.classWithMethods);
        } catch (Exception e) {
            throw new IndexOutOfBoundsException(e.getMessage());
        }

    }

    /**
     * Get data from a specified method found within the class instance. Checks column objects for matching column name then invokes corresponding method specified within the Column object
     * @param columnName the given column name. Must match exactly with a Column.getColumnName() call for any given Column object within the list
     * @return an Object instance containing the return of the specified method. Null if not found (i.e. the column name did not match or an exception occured)
     */
    public Object getData(String columnName) {

        try {
            for (Column column : this.columns) {
                if (column.getColumnName().equals(columnName)) {
                    return column.getColumnDataMethod().invoke(this.classWithMethods);
                }
            }
        } catch (Exception e) {
            return null;
        }

        return null;

    }

    /**
     * Get the instance class
     * @return the instance as a Class object
     */
    public Class getInstanceClass() {
        return this.classWithMethods.getClass();
    }

    /**
     * Get the instance object
     * @return the instance as an Object
     */
    public Object getInstance() {
        return this.classWithMethods;
    }

    /**
     * Get the number of invokable methods
     * @return the number of methods as an Integer
     */
    public int getNumberOfMethods() {
        return this.dataMethods.size();
    }

    /**
     * Get column objects that represent the columns within the database. Typically used to check permissions on a specific (or more) column.
     * @return a List of column objects (a COPY of the list within the given ModelClass instance)
     */
    public List<Column> getColumns() {
        List<Column> returnList = new ArrayList<>() {{
            addAll(columns);
        }};

        return returnList;
    }

}
