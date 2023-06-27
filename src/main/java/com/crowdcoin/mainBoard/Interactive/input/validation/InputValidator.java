package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;

public interface InputValidator {

    /**
     * Validates if the given input complies with a classes arbitrary set of rules
     * @param input the given string to validate
     * @return true if the string complies with the arbitrary set of rules
     * @throws ValidationException if the string fails to comply with the set of rules. The message within the exception should divulge where the input failed
     */
    boolean isInputValid(String input) throws ValidationException;

}
