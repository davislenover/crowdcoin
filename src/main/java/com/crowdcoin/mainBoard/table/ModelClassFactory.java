package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ModelClassFactory {

    /**
     * Builds a ModelClass from a specified class instance
     * @param classInstance class instance to find TableReadable methods from
     * @return a ModelClass object containing class instance and TableReadable methods
     * @throws NotZeroArgumentException if any method that is annotated contains more than zero arguments
     * @Note for a method to be added to the ModelClass, one must specify an @TableReadable annotation above the specified method
     */
    public ModelClass build(Object classInstance) throws NotZeroArgumentException {

        List<Method> methodList = new ArrayList<>();

        for (Method methodCandidate : classInstance.getClass().getMethods()) {

            // For all methods found within class, check if TableReadable annotation is attached
            if (methodCandidate.isAnnotationPresent(TableReadable.class)) {

                if (methodCandidate.getParameterCount() != 0) {
                    throw new NotZeroArgumentException(methodCandidate.getName());
                }

                // This means method is an intended value getter for TableColumn as specified by user
                // Thus add to list
                methodList.add(methodCandidate);

            }

        }

        // TableReadable has order attribute which allows the user to specify the order in which methods are added to this list
        // Thus we sort methodList with a comparator that compares order attributes
        Collections.sort(methodList, Comparator.comparingInt((Method o) -> o.getAnnotation(TableReadable.class).order()));
        return new ModelClass(classInstance,methodList);

        }

}
