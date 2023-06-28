package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;

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

        if (input.length() <= this.strLength) {
            return true;
        }

        throw new ValidationException(invalidMessage + strLength + " or more characters long");
    }
}