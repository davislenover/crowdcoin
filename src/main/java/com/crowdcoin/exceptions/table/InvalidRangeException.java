package com.crowdcoin.exceptions.table;

public class InvalidRangeException extends Exception {

    public InvalidRangeException(String startRange, String endRange) {

        super("Range does not exist between " + "\"" + startRange + "\"" + " and " + "\"" + endRange + "\"");

    }

}
