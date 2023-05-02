package com.crowdcoin.mainBoard.table;

import java.lang.reflect.Method;
import java.util.List;

public class ModelClass {

    private Object classWithMethods;
    private List<Method> dataMethods;

    public ModelClass(Object classInstance, List<Method> associatedMethods) {
        this.classWithMethods = classInstance;
        this.dataMethods = associatedMethods;
    }

    public Object getData(int methodIndex) {

        try {
            // Invoke specified method in class and return result as object
            return this.dataMethods.get(methodIndex).invoke(this.classWithMethods);
        } catch (Exception e) {

        }

        return null;

    }

}
