package com.crowdcoin.networking.sqlcom.data.constraints;

public class NameConstraint implements SQLColumnConstraint {
    private String prefix;
    private String validName;
    private int order = 0;

    /**
     * Creates a Name SQLColumnConstraint
     * @param columnPrefix the prefix a column name will have to check for validity to the constraint. If the prefix is not found within the name (when invoking {@link NameConstraint#isValid(String)}), then the name is valid
     * @param nameForValid if the prefix in a column name is found, the only way the name is valid is if it's name contains nameForValid
     */
    public NameConstraint(String columnPrefix, String nameForValid) {
        this.prefix = columnPrefix;
        this.validName = nameForValid;
    }

    @Override
    public boolean isValid(String columnName) {
        if (columnName.contains(this.prefix)) {
            if (columnName.contains(this.validName)) {
                return true;
            }
            return false;
        } else {
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
