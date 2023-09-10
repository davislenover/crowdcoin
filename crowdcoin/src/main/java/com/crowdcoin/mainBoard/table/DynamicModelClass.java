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
                            int posToGet = Integer.valueOf(columnName.substring(column.getColumnName().length(),column.getColumnName().length()+getVariableColumnPositionFromID(columnName,column.getColumnName())));
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

    // Method checks how many numbers are present right after the variable column prefix
    private int getVariableColumnPositionFromID(String columnNameRequested,String storedPrefix) {

        int numberLength = 0;
        char[] charVals = columnNameRequested.toCharArray();

        // Keep iterating through the column name and attempt to parse to a number. If error, then no more numbers exist right after the prefix
        for (int index = storedPrefix.length(); index < columnNameRequested.length(); index++) {
            try {
                Integer testInt = Integer.valueOf(String.valueOf(charVals[index]));
                numberLength++;
            } catch (Exception e) {
                break;
            }
        }
        return numberLength;
    }

    /**
     * Gets the starting index for variable column numbers after the prefix
     * @return the starting index as an Integer
     */
    public static int getStartIndex() {
        return objectStartIndex;
    }

    /**
     * Gets all variable column names with the prefix and index number attached.
     * @param klass the given ModelClass (DynamicModelClass) with the variable column
     * @param column the given variable column
     * @return a list of Strings corresponding to each column prefix and index
     */
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

    /**
     * Gets all variable column data. Takes a given Column object and invokes its method for all variable indices
     * @param klass the given ModelClass (DynamicModelClass) with the variable column
     * @param column the given variable column
     * @return a list of Objects corresponding to each return from the method invoked within the Column object with an index
     */
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

    /**
     * Gets a variable column name prefix
     * @param klass given ModelClass (DynamicModelClass) with a variable column
     * @return the given variable column prefix. Blank string if no variable column was found
     */
    public static String getVariableColumnPrefix(ModelClass klass) {
        List<Column> columns = klass.getColumns();
        Column varCol = klass.getColumns().get(columns.size()-1);
        if (varCol.isVariable()) {
            return varCol.getColumnName();
        }
        return "";
    }

    /**
     * Returns the size of variable column data
     * @param klass the given ModelClass housing the variable column
     * @return the size as an Integer. Size of 0 if either there is no data within the variable column or the variable column does not exist
     */
    public static int getNextVariableColumnInteger(ModelClass klass) {
        List<Column> columns = klass.getColumns();
        Column varCol = klass.getColumns().get(columns.size()-1);
        return getAllVariableData(klass,varCol).size();
    }

}
