package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;

import java.util.Comparator;

public class ComparatorValidator implements InputValidator {

    private String message = " Input must be greater than or equal to ";
    private String context;
    private Comparator<String> comparator;

    /**
     * Compares the given input within a specified comparator (to validate, comparator must return 0 or a positive integer). Note that it is assumed a live reference to an object is stored in the given comparator as the second compare input is assumed to be null
     * @param comparator the given comparator
     * @param context a brief description about what the input should be greater than or equal to
     */
    public ComparatorValidator(Comparator<String> comparator, String context) {
        this.comparator = comparator;
        this.context = context;
    }

    @Override
    public boolean isInputValid(String input) throws ValidationException {

        try {
            if (comparator.compare(input,null) >= 0) {
                return true;
            }
        } catch (Exception e) {

        }

        throw new ValidationException(this.message + this.context);
    }
}
