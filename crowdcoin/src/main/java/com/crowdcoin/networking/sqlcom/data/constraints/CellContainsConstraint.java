package com.crowdcoin.networking.sqlcom.data.constraints;

public class CellContainsConstraint implements SQLCellConstraint {

    private String validData;
    private int order = 0;

    /**
     * Creates a CellContains Constraint. A cell must contain the given validData to be valid (true)
     * @param validData the given validData
     */
    public CellContainsConstraint(String validData) {
        this.validData = validData;
    }

    @Override
    public boolean isValid(String cellData) {
        return cellData.contains(this.validData);
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
