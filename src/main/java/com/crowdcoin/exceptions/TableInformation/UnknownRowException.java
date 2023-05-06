package com.crowdcoin.exceptions.TableInformation;

public class UnknownRowException extends Exception {

    public UnknownRowException(int unknownIndex, int maxIndex) {

        super("Unknown row (" + unknownIndex + "), maximum is " + maxIndex);

    }

}
