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
import java.util.Arrays;
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

    public void setNumOfRowsPerRequest(int rowsPerRequest) throws FailedQueryException, SQLException, InvalidRangeException {

        if (rowsPerRequest > 0) {
            this.numOfRowsPerRequest = rowsPerRequest;
        } else {
            throw new IllegalArgumentException("rowsPerRequest must be greater than 0 (entered: " + rowsPerRequest + ")");
        }

        this.setupIterator();

    }

    private void setCurrentRowNext() throws FailedQueryException, SQLException, InvalidRangeException {

        if (hasNext()) {

            this.previousRowSet.clear();
            this.previousRowSet.addAll(currentRowSet);

            this.currentRowSet.clear();
            this.currentRowSet.addAll(nextRowSet);
            this.currentRowCount+=currentRowSet.size();
            System.out.println(this.currentRowCount);

            this.nextRowSet.clear();
            this.nextRowSet.addAll(this.sqlTable.getRows(this.currentRowCount,this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));
            System.out.println(nextRowSet.isEmpty());

            checkRowPosition();

        } else {
            System.out.println("Last rows!");
        }

    }

    private void setCurrentRowPrevious() throws FailedQueryException, SQLException, InvalidRangeException {

        if (!this.isFirstRow) {

            this.nextRowSet.clear();
            this.nextRowSet.addAll(this.currentRowSet);

            this.currentRowCount-=currentRowSet.size();

            this.currentRowSet.clear();
            this.currentRowSet.addAll(previousRowSet);
            System.out.println(this.currentRowCount);

            int previousSize = this.previousRowSet.size();
            this.previousRowSet.clear();
            if (this.currentRowCount-this.numOfRowsPerRequest > 0) {
                this.previousRowSet.addAll(this.sqlTable.getRows(this.currentRowCount-(this.currentRowSet.size()+previousSize),this.numOfRowsPerRequest,0,this.sqlTable.getNumberOfColumns()-1));
            }

            checkRowPosition();

        } else {

            System.out.println("First rows!");

        }

    }

    public void checkRowPosition() {
        if (this.currentRowSet.size() < this.numOfRowsPerRequest || this.nextRowSet.isEmpty()) {
            this.isLastRow = true;
        } else {
            this.isLastRow = false;
        }

        if (this.previousRowSet.size() != 0) {
            this.isFirstRow = false;
        } else {
            this.isFirstRow = true;
        }

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

    public List<List<Object>> getCurrentRowSet() {
        return List.copyOf(this.currentRowSet);
    }

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
