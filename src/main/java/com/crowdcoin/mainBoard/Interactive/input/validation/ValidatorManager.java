package com.crowdcoin.mainBoard.Interactive.input.validation;

import com.crowdcoin.exceptions.validation.ValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class ValidatorManager implements Collection<InputValidator> {

    private List<InputValidator> inputValidatorList;

    public ValidatorManager() {
        this.inputValidatorList = new ArrayList<>();
    }

    @Override
    public int size() {
        return inputValidatorList.size();
    }

    @Override
    public boolean isEmpty() {
        return inputValidatorList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return inputValidatorList.contains(o);
    }

    @Override
    public Iterator<InputValidator> iterator() {
        return inputValidatorList.iterator();
    }

    @Override
    public Object[] toArray() {
        return inputValidatorList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return inputValidatorList.toArray(a);
    }

    @Override
    public boolean add(InputValidator inputValidator) {
        return this.inputValidatorList.add(inputValidator);
    }

    @Override
    public boolean remove(Object o) {
        return this.inputValidatorList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.inputValidatorList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends InputValidator> c) {
        return this.inputValidatorList.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return this.inputValidatorList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.inputValidatorList.retainAll(c);
    }

    @Override
    public void clear() {
        this.inputValidatorList.clear();
    }

    /**
     * Validate input with all validators in collection
     * @param input the given input to validate
     * @return true if input was validated by all validators in collection
     * @throws ValidationException if input was not validated by all validators in collection. Exception message will contain all issues with regards to where input failed
     */
    public boolean validateInput(String input) throws ValidationException {

        String message = "";
        for (InputValidator validator : this.inputValidatorList) {

            // Try every validator on input, add error messages together
            try {
                validator.isInputValid(input);
            } catch (ValidationException e) {

                if (!message.isEmpty()) {
                    message+= " and ";
                }

                message+=e.getMessage();


            }

        }

        if (!message.isEmpty()) {
            throw new ValidationException(message);
        }

        return true;

    }

}
