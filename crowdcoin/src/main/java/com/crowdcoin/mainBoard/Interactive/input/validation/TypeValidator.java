package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;

import java.lang.reflect.Type;

public class TypeValidator implements InputValidator {

    private static String message = " Input must be of type ";
    private Class<? extends Object> type;

    /**
     * Checks if the given String can be converted into another specified type
     * @param classType the given class type that extends the Object class
     */
    public TypeValidator(Class<? extends Object> classType) {
        this.type = classType;
    }

    @Override
    public boolean isInputValid(String input) throws ValidationException {
        try {
            type.getConstructors()[0].newInstance(input);
            return true;
        } catch (Exception e) {
            throw new ValidationException(message + this.type.getSimpleName());
        }

    }
}
