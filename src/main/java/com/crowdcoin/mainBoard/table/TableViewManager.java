package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TableViewManager implements Iterator<List<List<Object>>> {

    private ModelClassFactory factory;
    private ModelClass modelClass;
    private SQLTable sqlTable;
    private ColumnContainer columnContainer;
    private List<List<Object>> currentRowSet;
    private List<List<Object>> previousRowSet;
    private List<List<Object>> nextRowSet;
    private boolean isLastRow = false;
    private boolean isFirstRow = true;
    private int currentRowCount;
    private int numOfRowsPerRequest = 10;

    /**
     * Handles access of rows in an SQL Table in an ordered/iterative fashion.
     * @param sqlTable an SQLTable object to get rows from a specific SQL Table
     * @param columnContainer the columns for the TableView object
     * @param modelClass the ModelClass for TableColumns to reference
     * @param factory the factory to produce clones of the ModelClass
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     */
    public TableViewManager(SQLTable sqlTable, ColumnContainer columnContainer, ModelClass modelClass, ModelClassFactory factory) throws FailedQueryException, SQLException, InvalidRangeException {
        this.factory = factory;
        this.columnContainer = columnContainer;
        this.modelClass = modelClass;
        this.sqlTable = sqlTable;
        this.currentRowCount = 0;
        this.currentRowSet = new ArrayList<>();
        this.nextRowSet = new ArrayList<>();
        this.previousRowSet = new ArrayList<>();

        this.setupIterator();

    }

    private void setupIterator() throws FailedQueryException, SQLException, InvalidRangeException {

        this.previousRowSet.clear();

        // Get starting rows
        List<List<Object>> rows = this.sqlTable.getRows(0,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1);
        this.currentRowSet.addAll(rows);
        this.currentRowCount+=rows.size();

        // Get next rows (to store in memory)
        List<List<Object>> nextRows = this.sqlTable.getRows(this.currentRowCount,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1);
        this.nextRowSet.addAll(nextRows);

        // Check if currentRow holds the last rows of data from the SQL Table
        checkRowPosition();

    }

    /**
     * Sets the number of rows that will be requested from the SQL database each query. It is not guaranteed that all queries will contain this many rows (i.e, the ending of the table). Changing this value will reset iteration of this instance back to the start of the table.
     * @param rowsPerRequest the number of rows to get per query as an integer
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException if the number of rows is not greater than 0
     */
    public void setNumOfRowsPerRequest(int rowsPerRequest) throws FailedQueryException, SQLException, InvalidRangeException {

        if (rowsPerRequest > 0) {
            this.numOfRowsPerRequest = rowsPerRequest;
        } else {
            throw new IllegalArgumentException("rowsPerRequest must be greater than 0 (entered: " + rowsPerRequest + ")");
        }

        // Call setup to reset lists with new number of rows
        this.setupIterator();

    }

    // Method to set the next rows (forward)
    private void setCurrentRowNext() throws FailedQueryException, SQLException, InvalidRangeException {

        // Check if the last row boolean was not set yet
        // i.e., at least one more set of rows exists
        if (hasNext()) {

            // Move currentRowSet to previousRowSet, get nextRowSet in memory and store in currentRowSet
            // i.e., shift all lists forwards
            this.previousRowSet.clear();
            this.previousRowSet.addAll(currentRowSet);

            this.currentRowSet.clear();
            this.currentRowSet.addAll(nextRowSet);
            // Add the size of currentRowSet to the currentRowCount (as currentRowSet got all items from nextRowSet so add)
            this.currentRowCount+=currentRowSet.size();

            // nextRowSet will get the next set of rows from the SQL Table in the database
            // Note that this may not return the full set
            this.nextRowSet.clear();
            this.nextRowSet.addAll(this.sqlTable.getRows(this.currentRowCount,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));

            // Since the full set may have not been stored in nextRowSet, this indicates that either nextRowSet is storing the last set of rows (if it's not empty but has less than the maximum number of rows variable)
            // or currentRowSet is storing the last row set (if nextRowSet is empty)
            // So update booleans for this
            checkRowPosition();

        }

    }

    // Method to set the next rows (go backwards)
    private void setCurrentRowPrevious() throws FailedQueryException, SQLException, InvalidRangeException {

        // Check if the first row boolean is false
        // i.e., at least one more previous row exists
        if (!this.isFirstRow) {

            // Move currentRowSet to NextRowSet
            // i.e., shifting the lists backwards
            this.nextRowSet.clear();
            this.nextRowSet.addAll(this.currentRowSet);

            // Before setting currentRowSet list, need to know how much to subtract by to set currentRowCount to the size of the last item in the previousRowSet
            this.currentRowCount-=currentRowSet.size();

            // Set currentRowSet to previousRowSet
            this.currentRowSet.clear();
            this.currentRowSet.addAll(previousRowSet);



            this.previousRowSet.clear();
            // Check if the currentRowCount subtracted by the maximum number of rows to display is greater than 0
            // This is because currentRowCount "points" to the bottom of the currentRowSet (i.e., count all rows up to the bottom of what is displayed)
            // Thus, if this result is 0, then it implies the current row set displayed is the first row set and thus, we cannot store negative rows into previousRowSet below, just keep the previousSet list empty
            if (this.currentRowCount-this.numOfRowsPerRequest > 0) {
                // If not 0, get previous rows
                // This is because when getting rows from the database, currentRowCount points size as from the last item displayed thus, to get the new previous rows, we need to subtract currentRowCount-
                // (by the size of the new currentRowSet (so now we "point" to the top of the new previous set) doubled (so now we "point" to the first element of the previous set))
                this.previousRowSet.addAll(this.sqlTable.getRows(this.currentRowCount-(this.currentRowSet.size()*2),this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));
            }

            // Update booleans
            checkRowPosition();

        }

    }

    public void checkRowPosition() {
        // If the currentRowSet size is less than what can be displayed at maximum then currentRowSet contains the end of the rows in the database
        // It is possible for currentRowSet to also contain exactly the maximum thus we check if the nextRowSet is empty which also says that currentRowSet contains the last rows
        if (this.currentRowSet.size() < this.numOfRowsPerRequest || this.nextRowSet.isEmpty()) {
            this.isLastRow = true;
        } else {
            this.isLastRow = false;
        }

        // If previousRowSet is empty, then currentRowSet must contain the starting rows
        this.isFirstRow = this.previousRowSet.isEmpty();

    }

    @Override
    public boolean hasNext() {

        if (!this.isLastRow) {
            return true;
        }
        return false;
    }

    public boolean isAtFirstRow() {
        return this.isFirstRow;
    }

    /**
     * Get the next set of rows from the given (on instantiation) SQL Table. Moves the next set of rows to get forwards then returns them.
     * @return a copy list of rows each as a list of Objects
     */
    @Override
    public List<List<Object>> next() {
        try {
            setCurrentRowNext();
            List<List<Object>> returnList = List.copyOf(this.currentRowSet);
            return returnList;
        } catch (FailedQueryException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidRangeException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the previous set of rows from the given (on instantiation) SQL Table. Moves the next set of rows to get backwards then returns them.
     * @return a copy list of rows each as a list of Objects
     */
    public List<List<Object>> previous() {

        try {
            setCurrentRowPrevious();
            List<List<Object>> returnList = List.copyOf(this.currentRowSet);
            return returnList;
        } catch (FailedQueryException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidRangeException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Gets the current set of rows from the given (on instantiation) SQL Table. Unlike next() and previous() this method does NOT shift the next set of rows to get (they stay the same).
     * @return a copy list of rows each as a list of Objects
     */
    public List<List<Object>> getCurrentRowSet() {
        return List.copyOf(this.currentRowSet);
    }

    /**
     * Shifts the next set of rows to get forwards, then applys that shifted list to the given destination Table. Only the tables items are cleared and not the columns.
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyNextRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Get rows of data
        List<List<Object>> rows = this.next();

        for (List<Object> row : rows) {
            // Basically, each table column doesn't know how to get data to display it other than that it is using a ModelClass type as input and Object type as output
            // How to get data is specified by setting the cell value factory of each column, where in this case, it is set to get the ModelClass object and invoke the corresponding method
            // Note that it is NOT a specific ModelClass, it simply specifies that once it receives a specific instance, it will do what it was told to do with any other ModelClass
            // The corresponding method is at the same index as the current size of the column data list (at the time) thus if a ModelClass has 4 invokable methods, the first column will get data from method 1
            // second from method 2 and so on
            // Thus when we add a ModelClass here, to display the data we are simply invoking the corresponding method within the specific instance of the ModelClass at the given index
            // This cloned ModelClass houses the row data from the table within the database and utilizes the specified methods (via @TableReadable annotation) to retrieve the data
            destinationTable.getItems().add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

    }

    /**
     * Shifts the next set of rows to get backwards, then applys that shifted list to the given destination Table. Only the tables items are cleared and not the columns.
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyPreviousRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Get rows of data
        List<List<Object>> rows = this.previous();

        for (List<Object> row : rows) {
            destinationTable.getItems().add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

    }

    /**
     * Applys the current rows stored to the given destination Table (no list shifting). The destination table has BOTH it's columns and items cleared before applying the current row (useful for remembering rows when switching Tabs)
     * @param destinationTable the TableView object to apply the rows to
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void applyCurrentRows(TableView destinationTable) throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();
        destinationTable.getColumns().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Load columns within Tab to table
        for (TableColumn column : this.columnContainer) {
            destinationTable.getColumns().add(column);
        }

        // Get rows of data
        List<List<Object>> rows = this.getCurrentRowSet();

        for (List<Object> row : rows) {
            destinationTable.getItems().add(this.factory.buildClone(this.modelClass,row.toArray()));
        }
    }

}
