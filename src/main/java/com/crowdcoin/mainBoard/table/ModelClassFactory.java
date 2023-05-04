package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

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

    /**
     * Builds a ModelClass clone from a specified ModelClass instance. Clones can have same or different parameter values. Is intended to allow for different values for different TableColumn cells
     * @param modelClass the ModelClass instance to clone
     * @param params variable arguments which correspond to the same arguments as the class instance constructor (NOT ModelClass constructor but rather the instance class it contains)
     * @return a ModelClass object containing class instance instantiated with the given parameters and cloned TableReadable methods
     * @throws NotZeroArgumentException if any method that is annotated contains more than zero arguments. This can happen if ModelClass was instantiated without the Factory
     * @throws NoSuchMethodException if params length or any type mismatches that of the declared constructor. Note that this may occur if attempting to clone a class that was defined within a class as the super class (outer class) will need to be instantiated too
     * @throws InvocationTargetException if an exception is thrown by the declared constructor of the instance class
     * @throws InstantiationException if the specified class cannot be instantiated
     * @throws IllegalAccessException if the class access modifiers prohibit instantiation in this manner
     */
    public ModelClass buildClone(ModelClass modelClass, Object ... params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NotZeroArgumentException {

        // params are variable arguments and thus are treated as an array
        // To find the correct constructor, one needs all parameter types of the constructor in an array
        // Create new Class array where the Class can be of any type (?) per index
        Class<?>[] paramTypes = new Class<?>[params.length];
        // Get all specific classes of params and add them to the paramTypes array
        for (int i = 0; i < params.length; i++) {
            // Note that Object is the static type of each param, getClass returns the runtime type
            paramTypes[i] = params[i].getClass();
        }

        // Attempt to find constructor with specified signature
        Constructor constructor = modelClass.getInstanceClass().getDeclaredConstructor(paramTypes);

        // Use actual parameter values to create new class instance
        Object newInstance = constructor.newInstance(params);

        // Create new ModelClass
        return this.build(newInstance);

    }

}
