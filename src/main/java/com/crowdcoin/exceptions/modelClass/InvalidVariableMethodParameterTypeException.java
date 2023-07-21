package com.crowdcoin.exceptions.modelClass;

public class InvalidVariableMethodParameterTypeException extends Exception {

    public InvalidVariableMethodParameterTypeException() {
        super("Parameter type for variable method must be of type Integer");
    }

}
