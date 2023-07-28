package com.crowdcoin.networking.sqlcom.data.constraints;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Holds SQLColumnConstraints and allows for iterative access to them
 */
public class ConstraintContainer implements Iterable<SQLColumnConstraint> {

    private List<SQLColumnConstraint> constraints;

    public ConstraintContainer() {
        this.constraints = new ArrayList<>();
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

    public SQLColumnConstraint get(int index) {
        if (index > this.constraints.size() - 1) {
            throw new IndexOutOfBoundsException("Index " + index + " out of range for size " + this.constraints.size());
        }
        return this.constraints.get(index);
    }

    /**
     * Clears all constraints from the collection
     */
    public void clear() {
        this.constraints.clear();
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

    @Override
    public Iterator<SQLColumnConstraint> iterator() {
        return this.constraints.iterator();
    }
}
