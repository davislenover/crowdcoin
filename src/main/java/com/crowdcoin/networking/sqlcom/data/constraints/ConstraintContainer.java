package com.crowdcoin.networking.sqlcom.data.constraints;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds SQLColumnConstraints and allows for iterative access to them
 */
public class ConstraintContainer implements Iterable<SQLColumnConstraint> {

    private List<SQLColumnConstraint> constraints;
    private List<SQLConstraintGroup> constraintGroups;

    public ConstraintContainer() {
        this.constraints = new ArrayList<>();
        this.constraintGroups = new ArrayList<>();
    }

    /**
     * Adds an SQLColumnConstraint to the container. Constraint must not be the same as another already present within the list
     * @param constraint the given unique constraint to add
     * @return true if the collection was modified as a result of this method call, false otherwise
     */
    public boolean add(SQLColumnConstraint constraint) {

        if (!this.constraints.contains(constraint)) {
            return this.constraints.add(constraint);
        }
        return false;
    }

    public boolean addGroup(SQLConstraintGroup group) {
        if (!this.constraintGroups.contains(group)) {
            return this.constraintGroups.add(group);
        }
        return false;
    }

    /**
     * Removes an SQLColumnConstraint to the container.
     * @param constraint the given unique constraint to remove
     * @return true if the collection was modified as a result of this method call, false otherwise
     */
    public boolean remove(SQLColumnConstraint constraint) {
        if (!this.constraints.contains(constraint)) {
            return this.constraints.remove(constraint);
        }
        return false;
    }

    /**
     * Removes an SQLColumnConstraint to the container.
     * @param index the given index to remove
     * @return true if the collection was modified as a result of this method call
     */
    public boolean remove(int index) {
        if (index > this.constraints.size() - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + this.constraints.size());
        }
        this.constraints.remove(index);
        return true;
    }

    public boolean removeGroup(SQLConstraintGroup group) {
        if (!this.constraintGroups.contains(group)) {
            return this.constraintGroups.remove(group);
        }
        return false;
    }

    public boolean removeGroup(int index) {
        if (index > this.constraintGroups.size() - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + this.constraintGroups.size());
        }
        this.constraintGroups.remove(index);
        return true;
    }

    public SQLColumnConstraint get(int index) {
        if (index > this.constraints.size() - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + this.constraints.size());
        }
        return this.constraints.get(index);
    }

    public SQLConstraintGroup getGroup(int index) {
        if (index > this.constraintGroups.size() - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + this.constraintGroups.size());
        }
        return this.constraintGroups.get(index);
    }

    /**
     * Clears all constraints from the collection
     */
    public void clear() {
        this.constraints.clear();
        this.constraintGroups.clear();
    }

    /**
     * Evaluates a given column name (as a String) for all constraints present within the collection
     * @param columnName the given String to evaluate
     * @return true if the column name is valid for all constraints. False otherwise
     */
    public boolean isValid(String columnName) {
        for(SQLColumnConstraint constraint : this.constraints) {
            if (!constraint.isValid(columnName)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks cell data for a column name is valid for all groups. If the columnName specified within the group is contained within the given columnName, then the corresponding cell data is checked for validity
     * @param columnName the given column name as a String
     * @param cellData the given cell data as a String
     * @return true if both the column name and cell data are valid for all groups, false otherwise
     */
    public boolean isValidGroup(String columnName, String cellData) {

        for (SQLConstraintGroup group : this.constraintGroups) {
            if (!group.isValid(columnName,cellData)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Iterator<SQLColumnConstraint> iterator() {
        return this.constraints.iterator();
    }
}
