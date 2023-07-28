package com.crowdcoin.networking.sqlcom.data.constraints;

public class CellContainsConstraint implements SQLCellConstraint {

    private String validData;

    public CellContainsConstraint(String validData) {
        this.validData = validData;
    }

    @Override
    public boolean isValid(String cellData) {
        return cellData.equals(this.validData);
    }
}
