package com.crowdcoin.networking.sqlcom.data.constraints;

public class CellGreaterThanConstraint implements SQLCellConstraint {
    private int greaterThan;
    private int order = 0;

    /**
     * Creates a CellGreaterThan Constraint. Cell data must be greater than the greaterThan variable specified to be valid (true)
     * @param greaterThan the given number for cell data to be greater than as an Integer
     */
    public CellGreaterThanConstraint(int greaterThan) {
        this.greaterThan = greaterThan;
    }

    /**
     * Tests for cell data validity. If data is not able to be converted into an Integer, auto returns true
     * @param data the given cell data
     * @return true if the cell data is valid, false otherwise
     */
    @Override
    public boolean isValid(String data) {

        try {
            Integer value = Integer.valueOf(data);
            if (value > this.greaterThan) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    @Override
    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return this.order;
    }
}
