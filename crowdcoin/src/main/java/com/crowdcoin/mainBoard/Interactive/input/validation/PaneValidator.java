package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.Interactive.input.InputField;

public class PaneValidator {

    /**
     * Check if all input within an InteractivePane is valid (i.e., passes all validator tests). Will automatically show Input info box to display failed validation message. Will hide info boxes if validation passes.
     * @param pane the given pane with InputFields
     * @return true if all InputFields pass validation, false otherwise
     */
    public static boolean isInputValid(InteractivePane pane) {

        boolean areFieldsGood = true;

        for (InputField checkField : pane) {
            try {
                checkField.validateField();
                checkField.hideInfo();
            } catch (ValidationException e) {
                checkField.getInfoBox().setInfoText(e.getMessage());
                checkField.showInfo();
                areFieldsGood = false;
            }
        }

        return areFieldsGood;

    }

}
