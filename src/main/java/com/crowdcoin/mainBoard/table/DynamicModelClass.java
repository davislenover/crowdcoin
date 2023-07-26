package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamicModelClass extends ModelClass {

    private static int objectStartIndex = 1;

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
     * If a method is marked as {@link TableReadable#isVariable()} (i.e., set to true), then the data this method returns depends on the number right after the {@link TableReadable#columnName()} prefixed to the database column name.
     * If there is no numerical prefix or the numerical value does not exist within the given {@link DynamicModelClass}, then null will be returned. This method also does not check for an exact match for a Column name but rather any sequence of chars which match
     * @param columnName the given column name. Must match exactly with a Column.getColumnName() call for any given Column object within the list
     * @return an Object instance containing the return of the specified method. Null if not found (i.e. the column name did not match or an exception occurred)
     */
    public Object getData(String columnName) {
        try {
            for (Column column : super.getColumns()) {
                if (column.isVariable()) {
                    if (columnName.contains(column.getColumnName())) {
                        try{
                            int posToGet = Integer.valueOf(columnName.substring(column.getColumnName().length(),column.getColumnName().length()+1));
                            return column.getColumnDataMethod().invoke(super.getInstance(),posToGet);
                        } catch (Exception e) {
                            return null;
                        }
                    }
                } else {
                    if (columnName.equals(column.getColumnName())) {
                        return column.getColumnDataMethod().invoke(super.getInstance());
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static int getStartIndex() {
        return objectStartIndex;
    }

    public static List<String> getAllVariableNames(ModelClass klass, Column column) {

        List<String> nameList = new ArrayList<>();
        if (column.isVariable()) {
            int addIndex = objectStartIndex;
            do {
                nameList.add(column.getColumnName() + addIndex);
                addIndex++;
            } while (klass.getData(column.getColumnName() + addIndex) != null);

        }

        return nameList;

    }

    public static List<Object> getAllVariableData(ModelClass klass, Column column) {

        List<Object> data = new ArrayList<>();
        if (column.isVariable()) {
            int addIndex = objectStartIndex;
            do {
                data.add(klass.getData(column.getColumnName()+addIndex));
                addIndex++;
            } while (klass.getData(column.getColumnName()+addIndex) != null);

        }

        return data;

    }

}
