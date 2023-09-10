package com.crowdcoin.exceptions.modelClass;

public class MultipleVariableMethodsException extends Exception {

    public MultipleVariableMethodsException() {
        super("Class contains more than one variable method. Class must only contain one");
    }

}
