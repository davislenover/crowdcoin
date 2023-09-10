package com.crowdcoin.exceptions.modelClass;

public class InvalidVariableMethodOrderException extends Exception {

    public InvalidVariableMethodOrderException(String invalidOrder) {
        super("Order of " + invalidOrder + " for variable method is invalid. Variable method must contain the highest order");
    }

}
