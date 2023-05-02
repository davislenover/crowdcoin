package com.crowdcoin.mainBoard.table;

public class GeneralRowObject<T> implements TableReadable<T> {

    private T rowValue;

    public GeneralRowObject(T rowValue) {

        this.rowValue = rowValue;

    }
    @Override
    public T getRowValue() {
        return this.rowValue;
    }
}
