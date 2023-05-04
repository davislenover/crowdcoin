package com.crowdcoin.mainBoard.table;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Class contains table columns and associated data
public class TableInformation implements Iterable<TableColumn<ModelClass,Object>> {

    // Columns can be any data type (Object), runtime type will decide
    private List<TableColumn<ModelClass,Object>> columnData;

    /**
     * Creates a TableInformation object. Acts as a container for columns
     * @returns a TableInformation object with a blank list
     * @Note columns added will have their CellValueFactory updated to retrieve data from ModelClasses
     */
    public TableInformation() {
        this.columnData = new ArrayList<>();
    }

    /**
     * Add a column object to the list of columns. The order in which a column is added specifies which method index in an arbitrary ModelClass instance it will invoke to get data to display
     * (i.e., if the column is the second column that was added, then it will invoke the second method found within the arbitrary ModelClass (or the method of "order = 2" via @TableReadable annotation)
     * @param column a column object to add to the list
     * @returns a boolean value. True if the column was added, false otherwise
     * @Note Name of column MUST be different than what is already present in list.
     */
    public boolean addColumn(TableColumn<ModelClass, Object> column) {

        // Check if name does not exist
        if (!doesNameExist(column.getText())) {
            // Add to list
            boolean returnbool = this.columnData.add(column);
            // Set column data to correspond with given model class method
            setCellValueProperties(column);
            return returnbool;
        } else {
            return false;
        }
    }

    /**
     * Remove a column given the name
     * @param columnName the name of the column to remove
     * @returns a boolean value. True if the column was removed, false otherwise
     * @Note removing a column will shift method's used by columns to retrieve cell values
     */
    public boolean removeColumn(String columnName) {
        boolean returnbool = removeColumn(getColumn(columnName));
        // Update how columns get cell values
        this.setAllCellValueProperties();
        return returnbool;

    }

    /**
     * Remove a column given the object
     * @param column the column object to remove
     * @returns a boolean value. True if the column was removed, false otherwise
     * @Note removing a column will shift method's in an arbitrary ModelClass used by columns to retrieve cell values
     */
    public boolean removeColumn(TableColumn<ModelClass, Object> column) {
        // Remove column from list (if applicable)
        boolean returnbool = this.columnData.remove(column);
        // Update how columns get cell values
        this.setAllCellValueProperties();
        return returnbool;
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

    // Get iterator
    @Override
    public Iterator<TableColumn<ModelClass, Object>> iterator() {
        return columnData.iterator();
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

    // Method to set a given column's cell value
    // Set's column to call corresponding method in ModelClass and use that as the cell's value
    private void setCellValueProperties(TableColumn<ModelClass, Object> column) {
        // Calculate which method corresponds to given methodIndex in ModelClass
        int methodIndex = this.columnData.size()-1;
        // p is of CellDataFeature type, calling getValue() gets the corresponding ModelClass instance and thus, invokes given method at given index
        // Returned value is displayed in column
        // When this column displays data, it will execute the following expression where p is the given ModelClass
        column.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getData(methodIndex)));
    }

    // Method to set all column cell values
    // Useful if a column is removed, thus re-calculation is required
    private void setAllCellValueProperties() {

        int methodIndex = 0;

        // Loop through all columns in column list
        for (TableColumn<ModelClass, Object> column : this.columnData) {
            // Copy because lambda's require variables to be in a "final" state
            int methodIndexCopy = methodIndex;
            // Set new cell value factory to updated index
            column.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getData(methodIndexCopy)));
            methodIndex++;

        }

    }

}
