package com.ratchet.exceptions.validation;

public class ValidationException extends Exception {

    /**
     * An exception typically thrown by a InputValidator if input does not meet certain arbitrary criteria. The message will divulge where the input failed to validate.
     * @param message
     */
    public ValidationException(String message) {
        super(message);
    }

}
