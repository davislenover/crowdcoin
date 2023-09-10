package com.crowdcoin.exceptions.modelClass;

public class InvalidVariableMethodParameterCount extends Exception {

    public InvalidVariableMethodParameterCount(String parameterCount) {
        super("Variable method contains " + parameterCount + " parameters, must contain exactly 1");
    }

}
