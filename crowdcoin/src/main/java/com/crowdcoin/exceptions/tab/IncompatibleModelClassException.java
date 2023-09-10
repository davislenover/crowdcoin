package com.crowdcoin.exceptions.tab;

public class IncompatibleModelClassException extends Exception {

    public IncompatibleModelClassException(int numberOfMethods, int numberRequired) {

        super("Number of methods " + "(" + numberOfMethods + ")" + " in modelClass mismatches that of Table " + "(" + numberRequired + ")");

    }

}
