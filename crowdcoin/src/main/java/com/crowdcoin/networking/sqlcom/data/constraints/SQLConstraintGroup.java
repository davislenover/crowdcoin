package com.crowdcoin.networking.sqlcom.data.constraints;
import com.crowdcoin.exceptions.table.InvalidRangeException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Holds a group of {@link SQLCellConstraint}s along with a column name sequence. Used to omit rows from a SQL result query. If a given column name is found to contain the sequence, then all cell constraints are tested against the given cell data
 */
public class SQLConstraintGroup implements Iterable<SQLConstraint> {

    private String columnNameContains;
    private List<SQLConstraint> constraintsOrder;

    public SQLConstraintGroup(String columnNameContains) {
        this.columnNameContains = columnNameContains;
        this.constraintsOrder = new ArrayList<>();
    }

    /**
     * Sets the column name sequence to check for when invoking {@link SQLConstraintGroup#isValid(String, String)}
     * @param columnNameContains the given column name sequence
     */
    public void setColumnNameContains(String columnNameContains) {
        this.columnNameContains = columnNameContains;
    }

    /**
     * Add a {@link SQLCellConstraint} to the group. Constraint must not already be present within the group to be added
     * @param constraint the given constraint to add
     * @return true if the group was modified as a result of this method call, false otherwise
     */
    public boolean add(SQLCellConstraint constraint) {
        if (!constraintsOrder.contains(constraint)) {
            this.constraintsOrder.sort(constraint);
            return constraintsOrder.add(constraint);
        }
        return false;
    }

    /**
     * Add a collection of {@link SQLCellConstraint} to the group. Constraint must not already be present within the group to be added
     * @param constraints the given constraints to add
     * @return true if the group was modified as a result of this method call, false if one or more constraints were already present within the group and as such, no constraint was added (even if some were not present in the group)
     */
    public boolean addAll(Collection<? extends SQLCellConstraint> constraints) {

        for (SQLConstraint constraint : constraints) {
            if (this.constraintsOrder.contains(constraint)) {
                return false;
            }
        }

        this.constraintsOrder.addAll(constraints);
        this.constraintsOrder.sort(this.constraintsOrder.get(0));
        return true;

    }

    /**
     * Removes a {@link SQLCellConstraint} from the group at a given index
     * @param index the given index as an Integer
     */
    public void remove(int index) {
        this.constraintsOrder.remove(index);
    }

    @Override
    public Iterator<SQLConstraint> iterator() {
        return this.constraintsOrder.iterator();
    }

    /**
     * Checks if a given column name and cell data are valid to constraints. Checks the column name against the ID constraint and if true then the cell data on all cell constraints
     * @param columnName the given column name as a String
     * @param cellData the given cell data as a String
     * @return true if all data is valid to the constraints, false otherwise
     */
    public boolean isValid(String columnName, String cellData) {
        if (columnName.contains(this.columnNameContains)) {
            for (SQLConstraint constraint : this.constraintsOrder) {
                if (!constraint.isValid(cellData)) {
                    return false;
                }
            }
        }
        return true;
    }

}
