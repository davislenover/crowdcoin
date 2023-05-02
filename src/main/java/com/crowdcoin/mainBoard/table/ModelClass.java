package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.List;

public class ModelClass {

    private Object classWithMethods;
    private List<Method> dataMethods;

    /**
     * Create a ModelClass for TableColumn's to reference
     * @param classInstance class instance which contains associatedMethods
     * @param associatedMethods executable methods from class instance
     * @Note It is recommended to call ModelClassFactory build() on a class instance to produce a ModelClass object
     */
    public ModelClass(Object classInstance, List<Method> associatedMethods) {
        this.classWithMethods = classInstance;
        this.dataMethods = associatedMethods;
    }

    /**
     * Get data from a specified method found within the class instance
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

}
