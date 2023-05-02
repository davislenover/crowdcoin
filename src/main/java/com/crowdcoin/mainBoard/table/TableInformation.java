package com.crowdcoin.mainBoard.table;
import javafx.scene.control.TableColumn;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

// Class contains table columns and associated data
public class TableInformation implements Iterable<TableColumn<? extends TableReadable<?>,? extends Comparable<?>>> {

    // Columns can be any data type as long as the data in question is comparable
    // Thus one can implement logical comparisons later on
    private List<TableColumn<? extends TableReadable<?>,? extends Comparable<?>>> columnData;

    /**
     * Creates a TableInformation object
     * @returns a TableInformation object with a blank list
     */
    public TableInformation() {
        this.columnData = new ArrayList<>();
    }

    /**
     * Add a column object to the list of columns
     * @param column a column object to add to the list
     * @returns a boolean value. True if the column was added, false otherwise
     * @Note Name of column MUST be different than what is already present in list
     */
    public <T extends Comparable<T>> boolean addColumn(TableColumn<TableReadable<T>, T> column) {

        // Check if name does not exist
        if (!doesNameExist(column.getText())) {
            // Add to list
            return this.columnData.add(column);
        } else {
            return false;
        }
    }

    /**
     * Remove a column given the name
     * @param columnName the name of the column to remove
     * @returns a boolean value. True if the column was removed, false otherwise
     */
    public boolean removeColumn(String columnName) {

        return removeColumn(getColumn(columnName));

    }

    /**
     * Remove a column given the object
     * @param column the column object to remove
     * @returns a boolean value. True if the column was removed, false otherwise
     */
    public <T extends Comparable<T>> boolean removeColumn(TableColumn<TableReadable<T>, T> column) {
        // Remove column from list (if applicable)
        return this.columnData.remove(column);
    }

    /**
     * Retrieve a column given the name
     * @param columnName the name of the column to find
     * @returns a column object containing the name. Note this object is LIVE. Null if not found
     */
    public TableColumn getColumn(String columnName) {

        for (TableColumn currentColumn : this.columnData) {

            if (currentColumn.getText().equals(columnName)) {

                return currentColumn;

            }

        }

        return null;

    }

    /**
     * Check if the list is empty
     * @returns true if the list is empty, false otherwise
     */
    public boolean isEmpty() {
        return this.columnData.isEmpty();
    }

    // Check if the name of a column already exists in the list
    // True if yes, false otherwise
    private boolean doesNameExist(String name) {
        for (TableColumn currentColumn : this.columnData) {

            if (currentColumn.getText().equals(name)) {

                return true;

            }
        }

        return false;

    }

    // Get iterator
    @Override
    public Iterator<TableColumn<? extends TableReadable<?>, ? extends Comparable<?>>> iterator() {
        return columnData.iterator();
    }

}
