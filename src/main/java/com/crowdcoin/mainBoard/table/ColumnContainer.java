package com.crowdcoin.mainBoard.table;
import com.crowdcoin.exceptions.columnContainer.NoColumnsException;
import com.crowdcoin.exceptions.columnContainer.NoTableViewInstanceException;
import com.crowdcoin.exceptions.columnContainer.UnknownRowException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Class contains table columns and associated data
public class ColumnContainer implements Iterable<TableColumn<ModelClass,Object>> {

    // Columns can be any data type (Object), runtime type will decide
    private List<TableColumn<ModelClass,Object>> columnData;

    /**
     * Creates a ColumnContainer object. Acts as a container for columns
     * @returns a ColumnContainer object with a blank list
     * @Note columns added will have their CellValueFactory updated to retrieve data from ModelClasses
     */
    public ColumnContainer() {
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

    /**
     * Get a row of data as a list of Objects. Data is returned in order of left to right in the TableView
     * @param rowIndex the row to get data. From 0 (first row) upwards
     * @return a list of Objects corresponding to the given rowIndex
     * @throws UnknownRowException if the specified rowIndex is not within the TableView
     * @throws NoColumnsException if there are no columns currently within the ColumnContainer instance (i.e., it is impossible to retrieve any data from nothing)
     * @throws NoTableViewInstanceException if columns do not contain a TableView instance. This is mostly likely caused by failure to load the Tab (that contains the ColumnContainer instance) into a TableView using the loadTab() method
     */
    public List<Object> getRow(int rowIndex) throws UnknownRowException, NoColumnsException, NoTableViewInstanceException {

        if (this.columnData.isEmpty()) {
            throw new NoColumnsException();
        }

        List<ModelClass> rowModelList;
        ObservableList<TableColumn<ModelClass, ?>> columns;

        // If loadTab() was not called within a TabInstance to apply these columns, it is possible that the given columns within ColumnContainer do not contain a TableView
        try {

            // Each row has its own modelClass which tells each column what data to display per row
            rowModelList = this.columnData.get(0).getTableView().getItems();
            // Get columns as their names are needed to correlate corresponding methods to invoke to get data
            columns = this.columnData.get(0).getColumns();

        } catch (NullPointerException e) {

            throw new NoTableViewInstanceException();

        }

        try {

            // Get the selected row of data
            // This gets the corresponding model class for the table row
            ModelClass row = rowModelList.get(rowIndex);

            List<Object> returnRow = new ArrayList<>();

            // From the modelClass, invoke all methods in-order and add their returns to the Object list
            for (int methodIndex = 0; methodIndex < row.getNumberOfMethods(); methodIndex++) {

                returnRow.add(row.getData(columns.get(methodIndex).getText()));

            }

            return returnRow;

        } catch (IndexOutOfBoundsException e) {

            throw new UnknownRowException(rowIndex,rowModelList.size()-1);

        }

    }

    /**
     * Get the currently selected row of data as a list of Objects. Data is returned in order of left to right in the TableView
     * @return a list of Objects corresponding to the given selected row
     * @throws NoColumnsException if there are no columns currently within the ColumnContainer instance (i.e., it is impossible to retrieve any data from nothing)
     * @throws NoTableViewInstanceException if columns do not contain a TableView instance. This is mostly likely caused by failure to load the Tab (that contains the ColumnContainer instance) into a TableView using the loadTab() method
     */
    public List<Object> getSelectedRow() throws NoColumnsException, NoTableViewInstanceException {

        if (this.columnData.isEmpty()) {
            throw new NoColumnsException();
        }

        List<Object> returnList = new ArrayList<>();
        ObservableList<TableColumn<ModelClass, ?>> columns;

        // If loadTab() was not called within a TabInstance to apply these columns, it is possible that the given columns within ColumnContainer do not contain a TableView
        try {

            TableView tableView = this.columnData.get(0).getTableView();

            // All cells are of the ModelClass type
            ObservableList<ModelClass> selectedRows = tableView.getSelectionModel().getSelectedItems();
            // Each cell carries the same model class per row, thus, get the only ModelClass in the list
            ModelClass rowModelClass = selectedRows.get(0);

            // Loop through columns and use their names to get the cell data
            for (TableColumn column : this.columnData) {
                // Add the cell data to the return list
                returnList.add(rowModelClass.getData(column.getText()));
            }

            return returnList;

        } catch (NullPointerException e) {
            throw new NoTableViewInstanceException();
        }

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
        // p is of CellDataFeature type, calling getValue() gets the corresponding ModelClass instance and thus, invokes given method at given index
        // Returned value is displayed in column
        // When this column displays data, it will execute the following expression where p is the given ModelClass
        // Get the text of the column name as this will match a corresponding Column object .getColumnName() and will invoke the corresponding method
        column.setCellValueFactory(p -> new SimpleObjectProperty<>(p.getValue().getData(column.getText())));
    }

}
