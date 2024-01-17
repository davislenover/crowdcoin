package com.crowdcoin.newwork;

import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.modelClass.ModelClass;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * An abstract {@link ModelClass} intended for representing abstract query results
 */
public class AbstractModelClass extends ModelClass {

    /**
     * Create an abstract ModelClass for TableColumn's to reference (i.e., TableColumns will invoke specified methods which get data within the ModelClass to display on screen)
     *
     * @param classInstance     class instance which contains associatedMethods
     * @param associatedGetDataMethod executable method from class instance which gets data for the table
     * @param columns
     * @Note It is recommended to call ModelClassFactory build() on a class instance to produce a ModelClass object
     */
    public AbstractModelClass(Object classInstance, List<Method> associatedGetDataMethod, List<Column> columns) {
        super(classInstance, associatedGetDataMethod, columns);
    }

    @Override
    public Object getData(int methodIndex) {
        try {
            return super.dataMethods.get(0).invoke(super.classWithMethods,methodIndex);
        } catch (Exception e) {
            throw new IndexOutOfBoundsException(e.getMessage());
        }
    }

    public Object getData(String columnName) {
        try {
            // Columns in list, their index corresponds to the index to pass into the get data method
            int methodIndex = 0;
            for (Column column : this.columns) {
                if (column.getColumnName().equals(columnName)) {
                    return super.dataMethods.get(0).invoke(super.classWithMethods,methodIndex);
                }
                methodIndex++;
            }
        } catch (Exception e) {
            return null;
        }

        return null;

    }


}
