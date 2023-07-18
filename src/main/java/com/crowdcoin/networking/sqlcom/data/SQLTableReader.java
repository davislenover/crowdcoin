package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.ModelClassFactory;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles access of rows in an SQL Table in an ordered/iterative fashion
 */
public class SQLTableReader implements Iterator<List<List<Object>>>, Observable<ModifyEvent,String>, Cloneable {

    // The purpose of this class is to store the previous, current and next set of rows to display to the TableView object
    // It aims to reduce queries by storing recently viewed portions of the TableView to memory
    private ModelClassFactory factory;
    private ModelClass modelClass;

    private SQLTable sqlTable;
    private List<List<Object>> currentRowSet;
    private List<List<Object>> previousRowSet;
    private List<List<Object>> nextRowSet;
    private boolean isLastRow = false;
    private boolean isFirstRow = true;
    private int currentRowCount;
    private int numOfRowsPerRequest = 10;
    private List<Observer<ModifyEvent,String>> subscriptionList;

    /**
     * Handles access of rows in an SQL Table in an ordered/iterative fashion.
     * @param sqlTable an SQLTable object to get rows from a specific SQL Table
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     */
    public SQLTableReader(SQLTable sqlTable, ModelClass modelClass, ModelClassFactory factory) throws FailedQueryException, SQLException, InvalidRangeException {
        this.sqlTable = sqlTable;
        this.modelClass = modelClass;
        this.factory = factory;
        this.currentRowCount = 0;
        this.currentRowSet = new ArrayList<>();
        this.nextRowSet = new ArrayList<>();
        this.previousRowSet = new ArrayList<>();
        this.subscriptionList = new ArrayList<>();
        this.setupIterator();

    }

    private void setupIterator() throws FailedQueryException, SQLException, InvalidRangeException {

        this.previousRowSet.clear();

        // Get starting rows
        List<List<Object>> rows = this.sqlTable.getRawRows(0,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1);
        this.currentRowSet.addAll(rows);
        this.currentRowCount+=rows.size();

        // Get next rows (to store in memory)
        List<List<Object>> nextRows = this.sqlTable.getRawRows(this.currentRowCount,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1);
        this.nextRowSet.addAll(nextRows);

        // Check if currentRow holds the last rows of data from the SQL Table
        checkRowPosition();

    }

    /**
     * Refreshes the current instance of SQLTableReader with new data from the SQLTable
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     */
    public void refreshCurrentView() throws FailedQueryException, SQLException, InvalidRangeException {

        int savedPosition = this.currentRowCount;
        this.reset();

        while(this.currentRowCount < savedPosition) {
            setCurrentRowNext();
            if (isAtLastRow()) {
                break;
            }
        }

    }

    /**
     * Deletes all data and retrieves a fresh query result from the SQLTable. This does not update any relevant button states, re-application will be required
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     */
    public void reset() throws FailedQueryException, SQLException, InvalidRangeException {
        this.currentRowCount = 0;
        this.currentRowSet.clear();
        this.nextRowSet.clear();
        this.previousRowSet.clear();
        this.setupIterator();
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

    /**
     * Get all rows within the SQL Table rows as a list of ModelClass objects. Each ModelClass represents a row. Method may not return all rows requested if the database does not contain the specified rows
     * @param startingIndex the given starting index (inclusive)
     * @param endingIndex the ending index (inclusive)
     * @return
     * @throws InvalidRangeException
     * @throws FailedQueryException
     * @throws SQLException
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public List<ModelClass> getRows(int startingIndex, int endingIndex) throws InvalidRangeException, FailedQueryException, SQLException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        if (startingIndex > endingIndex) {
            throw new InvalidRangeException(String.valueOf(startingIndex),String.valueOf(endingIndex));
        }

        List<ModelClass> entries = new ArrayList<>();
        int numOfRows = endingIndex-startingIndex;
        List<List<Object>> rows = this.sqlTable.getRawRows(startingIndex,numOfRows,0,this.sqlTable.getNumberOfColumns()-1);
        for (List<Object> row : rows) {
            entries.add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

        return entries;

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
            this.nextRowSet.addAll(this.sqlTable.getRawRows(this.currentRowCount,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));

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
                this.previousRowSet.addAll(this.sqlTable.getRawRows(this.currentRowCount-(this.currentRowSet.size()*2),this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));
            }

            // Update booleans
            checkRowPosition();

        }

    }

    private void checkRowPosition() {
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

    /**
     * Check if current set of rows is the first set within the SQL Table
     * @return true if the set of rows is the first within the SQL Table, false otherwise
     */
    public boolean isAtFirstRow() {
        return this.isFirstRow;
    }

    /**
     * Check if current set of rows is the last set within the SQL Table
     * @return true if the set of rows is the last within the SQL Table, false otherwise
     */
    public boolean isAtLastRow() {
        return this.isLastRow;
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
     * Gets the current set of rows as ModelClass objects from the given (on instantiation) SQL Table. Unlike next() and previous() this method does NOT shift the next set of rows to get (they stay the same).
     * @return a copy list of rows each as a list of Objects
     */
    public List<ModelClass> getCurrentModelClassSet() throws NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        List<ModelClass> entries = new ArrayList<>();
        for (List<Object> row : this.currentRowSet) {
            entries.add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

        return entries;

    }

    @Override
    public boolean addObserver(Observer<ModifyEvent,String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ModifyEvent,String> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(ModifyEvent event) {
        for (Observer<ModifyEvent,String> observer : List.copyOf(this.subscriptionList)) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }

    /**
     * Gets the modelClass the TableViewManager instance is utilizing
     * @return
     */
    public ModelClass getModelClass() {
        return this.modelClass;
    }

    public ModelClassFactory getModelClassFactory() {
        return this.factory;
    }

    /**
     * Creates a new instance of SQLTableReader with the same SQLTable object. Resets the iteration back to the start of the entries
     * @return an SQLTableReader
     */
    public Object clone() {

        try {
            return new SQLTableReader(this.sqlTable,this.modelClass,this.factory);
        } catch (FailedQueryException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (InvalidRangeException e) {
            throw new RuntimeException(e);
        }

    }

}
