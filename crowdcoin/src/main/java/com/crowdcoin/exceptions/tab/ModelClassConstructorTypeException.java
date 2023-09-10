package com.crowdcoin.exceptions.tab;

public class ModelClassConstructorTypeException extends Exception {

    public ModelClassConstructorTypeException() {
        super("Constructor parameter types do not match those of columns in database");
    }

}
