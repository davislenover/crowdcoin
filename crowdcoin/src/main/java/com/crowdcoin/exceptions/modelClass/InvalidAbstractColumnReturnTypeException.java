package com.crowdcoin.exceptions.modelClass;

public class InvalidAbstractColumnReturnTypeException extends Exception {

    public InvalidAbstractColumnReturnTypeException(Class<?> returnType) {
        super("Invalid column data method return type, " + returnType.getTypeName() + ". Return type should be of type Map<Integer,String>");
    }

}
