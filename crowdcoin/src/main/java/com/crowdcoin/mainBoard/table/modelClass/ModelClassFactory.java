package com.crowdcoin.mainBoard.table.modelClass;

import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterCount;
import com.crowdcoin.exceptions.modelClass.InvalidVariableMethodParameterTypeException;
import com.crowdcoin.exceptions.modelClass.MultipleVariableMethodsException;
import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassMethodNamesException;
import com.crowdcoin.exceptions.tab.ModelClassConstructorTypeException;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.table.permissions.IsSystemWriteable;
import com.crowdcoin.mainBoard.table.permissions.IsWriteable;
import com.crowdcoin.networking.sqlcom.SQLTypes;
import com.crowdcoin.networking.sqlcom.data.SQLTable;

import java.lang.reflect.Array;
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
    public ModelClass build(Object classInstance) throws NotZeroArgumentException, MultipleVariableMethodsException, InvalidVariableMethodParameterCount, InvalidVariableMethodParameterTypeException {
        List<Method> methodList = new ArrayList<>();
        List<Column> columnList = new ArrayList<>();
        boolean isVariable = false;

        for (Method methodCandidate : classInstance.getClass().getMethods()) {

            // For all methods found within class, check if TableReadable annotation is attached
            if (methodCandidate.isAnnotationPresent(TableReadable.class)) {

                if (methodCandidate.getAnnotation(TableReadable.class).isVariable()) {
                    if (!isVariable) {
                        isVariable = true;

                        if (methodCandidate.getParameterCount() != 1) {
                            throw new InvalidVariableMethodParameterCount(String.valueOf(methodCandidate.getParameterCount()));
                        }
                        if (methodCandidate.getParameterTypes()[0].equals(Integer.class)) {
                            throw new InvalidVariableMethodParameterTypeException();
                        }
                    } else {
                        throw new MultipleVariableMethodsException();
                    }
                } else {
                    if (methodCandidate.getParameterCount() != 0) {
                        throw new NotZeroArgumentException(methodCandidate.getName());
                    }
                }

                // This means method is an intended value getter for TableColumn as specified by user
                // Thus add to list
                methodList.add(methodCandidate);

                // Create a column object to store column and privileges
                Column newColumn = new Column(methodCandidate.getAnnotation(TableReadable.class).columnName(),methodCandidate.getReturnType(),methodCandidate);
                // Set Permissions
                newColumn.addPermission(new IsReadable(methodCandidate.getAnnotation(TableReadable.class).isUserReadable()));
                newColumn.addPermission(new IsWriteable(methodCandidate.getAnnotation(TableReadable.class).isUserWriteable()));
                newColumn.addPermission(new IsSystemWriteable(methodCandidate.getAnnotation(TableReadable.class).isSystemWriteable()));
                newColumn.setOrdinalPosition(methodCandidate.getAnnotation(TableReadable.class).order());
                newColumn.setVariable(methodCandidate.getAnnotation(TableReadable.class).isVariable());
                columnList.add(newColumn);

            }

        }

        // TableReadable has order attribute which allows the user to specify the order in which methods are added to this list
        // Thus we sort methodList with a comparator that compares order attributes
        Collections.sort(methodList, Comparator.comparingInt((Method o) -> o.getAnnotation(TableReadable.class).order()));
        Collections.sort(columnList,Comparator.comparingInt((Column o) -> o.getOrdinalPosition()));

        if(isVariable) {
            return new DynamicModelClass(classInstance,methodList,columnList);
        }

        return new ModelClass(classInstance,methodList,columnList);
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
    public ModelClass buildClone(ModelClass modelClass, Object ... params) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NotZeroArgumentException, MultipleVariableMethodsException, InvalidVariableMethodParameterTypeException, InvalidVariableMethodParameterCount {

        if (isVariable(modelClass)) {
            // Setup
            Class<?>[] paramTypes = new Class<?>[modelClass.getNumberOfMethods()];
            Object[] constructorInputs = new Object[modelClass.getNumberOfMethods()];
            int numOfModelClassMethods = modelClass.getNumberOfMethods();
            // Get all specific classes of params and add them to the paramTypes array
            for (int i = 0; i < numOfModelClassMethods - 1; i++) {
                // Note that Object is the static type of each param, getClass returns the runtime type
                paramTypes[i] = params[i].getClass();
                constructorInputs[i] = params[i];
            }
            // Because the last parameter in the constructor is of varargs, it has to be treated as an array of the given type
            paramTypes[numOfModelClassMethods - 1] = Array.newInstance(params[numOfModelClassMethods - 1].getClass(),1).getClass();

            // For the rest of inputs (after the methods that aren't variable), add to one array
            Object[] varargsInputs = (Object[]) Array.newInstance(params[numOfModelClassMethods - 1].getClass(),params.length - (numOfModelClassMethods-1));
            for(int varargsIndex = 0; varargsIndex <  params.length - (numOfModelClassMethods-1); varargsIndex++) {
                varargsInputs[varargsIndex] = params[varargsIndex + (numOfModelClassMethods-1)];
            }

            // Set newly created array to last index in constructor inputs
            constructorInputs[numOfModelClassMethods - 1] = varargsInputs;

            // Attempt to find constructor with specified signature
            Constructor constructor = modelClass.getInstanceClass().getDeclaredConstructor(paramTypes);

            // Use actual parameter values to create new class instance
            Object newInstance = constructor.newInstance(constructorInputs);

            // Create new ModelClass
            return this.build(newInstance);

        }

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

    /**
     * Checks validity of a ModelClass by comparing table data from the SQLTable
     * @param klass the given ModelClass object to check
     * @param table the given SQLTable object to compare data against
     * @throws IncompatibleModelClassException if the number of methods matches that of the number of columns of the SQLTable
     * @throws ModelClassConstructorTypeException if the parameter types of the modelClass constructor mismatch that of the columns within the database
     * @throws IncompatibleModelClassMethodNamesException if one or more method names do not match that of the columns names within the database
     */
    public static void checkModelClassValidity(ModelClass klass, SQLTable table) throws IncompatibleModelClassException, ModelClassConstructorTypeException, IncompatibleModelClassMethodNamesException {
        checkNumOfParams(klass,table);
        checkMethodNames(klass,table);
        checkTypes(klass,table);
    }

    // Method to check if the parameter types of the modelClass constructor match that of columns within the database
    // Throws ModelClassConstructorTypeException if there is a mismatch
    private static void checkTypes(ModelClass klass, SQLTable table) throws ModelClassConstructorTypeException {

        boolean doesntHaveIssue = true;

        // Get constructor of modelClass (which is assumed to be the only constructor)
        Class<?>[] modelClassConstructorTypes = klass.getInstanceClass().getConstructors()[0].getParameterTypes();

        // Loop through the column types
        int constructorTypeIndex = 0;
        int holdIndex = 0;
        boolean isArray = false;

        for (String columnType : table.getRawColumnTypes()) {

            try {
                // Queries class contains a hashmap that provides the corresponding class for the given column type in sql
                // Check if both the columnType class and the corresponding constructor parameter match
                // Check isArray() function last because if isArray boolean is true (not function), that is the last constructor type, thus, checking for an index higher will result in an IndexOutOfBoundsException
                if (!isArray && !modelClassConstructorTypes[constructorTypeIndex].isArray()) {
                    if (!SQLTypes.SQLToJavaType.get(columnType.toUpperCase()).getName().toUpperCase().contains(modelClassConstructorTypes[constructorTypeIndex].getName().toUpperCase())) {
                        doesntHaveIssue = false;
                        break;
                    }
                    // If the type is an array, then this indicates that there is variable arguments and thus, this modelClass is a DynamicModelClass so, check if all the rest of the columnTypes match with the array type
                    // Copy the current type index and don't increment to get the array type all the time
                } else if (!isArray) {
                    isArray = true;
                    holdIndex = constructorTypeIndex;
                }

                if (isArray) {
                    // Use getComponentType() instead of just getName as we have to parse array first
                    if (!SQLTypes.SQLToJavaType.get(columnType.toUpperCase()).getName().toUpperCase().contains(modelClassConstructorTypes[holdIndex].getComponentType().getName().toUpperCase())) {
                        doesntHaveIssue = false;
                        break;
                    }
                }

                // Index out of bounds also indicates there are more columns than there are arguments in the constructor
            } catch (IndexOutOfBoundsException e) {
                doesntHaveIssue = false;
                break;
            }
            constructorTypeIndex++;
        }

        if (!doesntHaveIssue) {
            throw new ModelClassConstructorTypeException();
        }

    }

    private static void checkMethodNames(ModelClass klass, SQLTable table) throws IncompatibleModelClassMethodNamesException {

        List<String> columnNames = table.getRawColumnNames();

        for (int index = 0; index < columnNames.size(); index++) {

            boolean noMatch = true;
            String sqlName = columnNames.get(index);

            for(Column column : klass.getColumns()) {
                if (column.getColumnName().equals(sqlName)) {
                    noMatch = false;
                    break;
                    // Check for variable column name as well
                } else if (column.isVariable() && sqlName.contains(column.getColumnName())) {
                    noMatch = false;
                    break;
                }
            }
            if (noMatch) {
                throw new IncompatibleModelClassMethodNamesException();
            }

        }

    }

    private static void checkNumOfParams(ModelClass klass, SQLTable table) throws IncompatibleModelClassException {
        // Check that the number of methods within the model reference are the same as the number of columns within the sql database table
        if (table.getNumberOfColumns() != klass.getNumberOfMethods()) {
            // If the methods do not match, check if a ModelClass contains a variable method (thus, it's possible that the ModelClass is supposed to not have the same number of methods as there are columns)
            for (Column column : klass.getColumns()) {
                if (column.isVariable()) {
                    return;
                }
            }
            throw new IncompatibleModelClassException(klass.getNumberOfMethods(),table.getNumberOfColumns());
        }
    }

    private boolean isVariable(ModelClass klass) {
        for (Column column : klass.getColumns()) {
            if (column.isVariable()) {
                return true;
            }
        }
        return false;
    }

}
