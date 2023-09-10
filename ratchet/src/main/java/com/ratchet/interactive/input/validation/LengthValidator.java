package com.ratchet.interactive.input.validation;

import com.ratchet.exceptions.validation.ValidationException;

public class LengthValidator implements InputValidator {

    private static String invalidMessage = "Input be at least ";
    private int strLength;

    /**
     * Checks if the length of given String is greater than or equal to corresponding length
     * @param strLength the corresponding length as an integer to validate input against
     */
    public LengthValidator(int strLength) {
        this.strLength = strLength;
    }

    @Override
    public boolean isInputValid(String input) throws ValidationException {

        try {
            if (input.length() >= this.strLength) {
                return true;
            }
        } catch (Exception e) {

        }

        throw new ValidationException(invalidMessage + strLength + " or more characters long");
    }
}
