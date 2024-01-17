package com.crowdcoin.exceptions.modelClass;

public class InvalidAnnotationUsageException extends Exception {
    public InvalidAnnotationUsageException() {
        super("Annotation usage for certain methods conflicts with other methods within the instance class");
    }

}
