package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.ratchet.exceptions.validation.ValidationException;
import com.ratchet.interactive.input.validation.InputValidator;
import com.ratchet.interactive.input.validation.MatchValidator;
import com.ratchet.interactive.input.validation.TypeValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {
    @Test
    public void testTypeValidator() throws ValidationException {

        // Test Integer
        InputValidator validator = new TypeValidator(Integer.class);
        assertEquals(true,validator.isInputValid("12"));
        assertThrows(ValidationException.class,() -> validator.isInputValid("12.11"));
        assertThrows(ValidationException.class,() -> validator.isInputValid("NOT A NUMBER"));
        assertThrows(ValidationException.class,() -> validator.isInputValid(""));

        // Test Float
        InputValidator validator2 = new TypeValidator(Float.class);
        assertEquals(true,validator2.isInputValid("12.0"));
        assertEquals(true,validator2.isInputValid("12.00"));

        // Should be noted that Double does not work well, however, this class is only really always going to check for whole numbers (integers)

    }

    @Test
    public void testMatchValidator() throws ValidationException {

        InputValidator validator = new MatchValidator("MatchThis");

        assertThrows(ValidationException.class, () -> validator.isInputValid("matchThis"));
        assertThrows(ValidationException.class, () -> validator.isInputValid("matchthis"));
        assertThrows(ValidationException.class, () -> validator.isInputValid("OK"));
        assertEquals(true,validator.isInputValid("MatchThis"));

    }


}