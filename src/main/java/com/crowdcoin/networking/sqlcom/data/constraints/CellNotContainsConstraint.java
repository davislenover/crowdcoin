package com.crowdcoin.networking.sqlcom.data.constraints;

public class CellNotContainsConstraint implements SQLCellConstraint {
    private String notContains;
    private int order = 0;

    /**
     * Creates a CellNotContains Constraint. A cell must not contain the given notContainsData to be valid (true)
     * @param notContainsData the given notContainsData
     */
    public CellNotContainsConstraint(String notContainsData) {
        this.notContains = notContainsData;
    }

    @Override
    public boolean isValid(String data) {
        if (data.contains(notContains)) {
            return false;
        }
        return true;
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
