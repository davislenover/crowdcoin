package com.crowdcoin.networking.sqlcom.data;

public class SQLFilter {

    private String filter = " WHERE ";

    public SQLFilter(SQLTable table, String columnName, WhereOperators operator, Object ... values) {

        // Check value input
        if (values.length == 0) {
            throw new IllegalArgumentException("Must be more than 0 values");
        }
        if (operator == WhereOperators.BETWEEN && values.length != 2) {
            throw new IllegalArgumentException("BETWEEN operator and value miss-match. There must be two values, actual: " + values.length);
        }
        // Check columnName
        if (!table.getColumnNames().contains(columnName)) {
            throw new IllegalArgumentException("Column name \"" + columnName + "\" does not exist in the SQL table");
        }

        if (operator == WhereOperators.BETWEEN) {
            this.filter += columnName + " " + operator.getOperatorString() + " " + values[0].toString() + " AND " + values[1].toString();
            return;
        } else if (operator == WhereOperators.IN || operator == WhereOperators.NOTIN) {
            this.filter += columnName + " " + operator.getOperatorString() + " (";
            for (int index = 0; index < values.length; index++) {
                if (index != (values.length - 1)){
                    this.filter += values[index].toString() + ", ";
                } else {
                    this.filter += values[index].toString() + ")";
                }
            }
        }

    }

}
