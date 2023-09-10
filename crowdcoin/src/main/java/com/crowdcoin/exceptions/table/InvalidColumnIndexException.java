package com.crowdcoin.exceptions.table;

public class InvalidColumnIndexException extends Exception {

    public InvalidColumnIndexException(int columnIndex, int maxRange) {

        super("Column index of " + columnIndex + " is out of range (max length " + maxRange + ")");

    }

}
