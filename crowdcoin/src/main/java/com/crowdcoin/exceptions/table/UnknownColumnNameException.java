package com.crowdcoin.exceptions.table;

public class UnknownColumnNameException extends Exception {

    public UnknownColumnNameException(String unknownName) {

        super("Unknown column name " + "\"" + unknownName + "\"");

    }

}
