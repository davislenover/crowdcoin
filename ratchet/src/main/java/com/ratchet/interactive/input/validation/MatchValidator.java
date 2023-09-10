package com.ratchet.interactive.input.validation;

import com.ratchet.exceptions.validation.ValidationException;

public class MatchValidator implements InputValidator {

    private static String invalidMessage = "This field cannot change! Must be ";
    private String matchString;

    public MatchValidator(String matchString) {
        this.matchString = matchString;
    }

    @Override
    public boolean isInputValid(String input) throws ValidationException {

        if (input.equals(matchString)) {
            return true;
        } else {
            throw new ValidationException(invalidMessage + matchString);
        }
    }
}
