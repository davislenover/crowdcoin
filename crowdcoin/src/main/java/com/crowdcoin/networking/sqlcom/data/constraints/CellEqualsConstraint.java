package com.crowdcoin.networking.sqlcom.data.constraints;

public class CellEqualsConstraint implements SQLCellConstraint {

    private String validData;
    private int order = 0;

    /**
     * Creates a CellEquals Constraint. A cell must equal the given validData to be valid (true)
     * @param validData the given validData
     */
    public CellEqualsConstraint(String validData) {
        this.validData = validData;
    }

    @Override
    public boolean isValid(String cellData) {
        return cellData.equals(this.validData);
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
