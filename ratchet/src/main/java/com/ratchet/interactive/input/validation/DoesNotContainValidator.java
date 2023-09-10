package com.ratchet.interactive.input.validation;

import com.ratchet.exceptions.validation.ValidationException;

import java.util.ArrayList;
import java.util.List;

public class DoesNotContainValidator implements InputValidator {

    private static String invalidMessage = "Given input must not contain \"";
    private List<String> contains;

    /**
     * Creates a DoesNotContainValidator. Checks if a given input string (when invoking {@link DoesNotContainValidator#isInputValid(String)}) does not contain one of the given doesNotContainValues.
     * @param doesNotContainValues the given strings to test an input string against
     */
    public DoesNotContainValidator(String ... doesNotContainValues) {
        this.contains = new ArrayList<>();
        for (String value : doesNotContainValues) {
            contains.add(value);
        }
    }

    @Override
    public boolean isInputValid(String input) throws ValidationException {
        for (String value : this.contains) {
            if (input.contains(value)) {
                throw new ValidationException(invalidMessage + value + "\"");
            }
        }
        return true;
    }
}
