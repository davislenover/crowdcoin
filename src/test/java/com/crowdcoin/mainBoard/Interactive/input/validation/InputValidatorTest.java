package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Executable;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {
    @Test
    public void estTypeValidator() throws ValidationException {

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


}