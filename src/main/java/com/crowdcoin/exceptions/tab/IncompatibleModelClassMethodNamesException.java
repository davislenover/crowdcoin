package com.crowdcoin.exceptions.tab;

public class IncompatibleModelClassMethodNamesException extends Exception {

    public IncompatibleModelClassMethodNamesException() {
        super("One or more method names do not match the column names within the SQL Database");
    }

}
