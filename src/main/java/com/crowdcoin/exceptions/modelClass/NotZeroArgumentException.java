package com.crowdcoin.exceptions.modelClass;

public class NotZeroArgumentException extends Exception {

    public NotZeroArgumentException(String methodName) {
        super("Method: " + "\"" + methodName + "\"" + " contains arguments. Model methods must contain zero arguments");
    }

}
