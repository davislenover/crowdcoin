package com.crowdcoin.newwork;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * A translation class between a {@link java.sql.ResultSet} and a {@link javafx.scene.control.TableView}
 */
public class QueryResult implements Iterator<Tuple> {
    private Map<Integer,String> columnPositionsToNames;
    private List<Tuple> tuples;

    private int currentPosition;

    /**
     * Creates a new {@link QueryResult}
     * @param resultSet the given {@link ResultSet} to create the model class after. The {@link ResultSet} must not be closed. The cursor within {@link ResultSet} is not guaranteed to be in a specific position after constructor call
     */
    public QueryResult(ResultSet resultSet) {
        this.columnPositionsToNames = new HashMap<>();
        this.tuples = new ArrayList<>();

        // Get columns and tuples (rows)
        this.extractColumns(resultSet);
        this.getTuples(resultSet);

        this.currentPosition = 0;

    }

    // Method to get column index's with their corresponding names
    private void extractColumns(ResultSet resultSet) {
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            // Column indexing starts at 1 as specified by documentation
            for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
                this.columnPositionsToNames.put(columnIndex,metaData.getColumnLabel(columnIndex));
            }
        } catch (SQLException e) {}
    }

    // Method to get all rows
    private void getTuples(ResultSet resultSet) {
        try {
            // Place cursor before first row (the call to .next() will move the cursor to the first row)
            resultSet.absolute(0);
            while (resultSet.next()) {
                // Create new tuple
                Tuple newTuple = new Tuple(resultSet.getRow());
                // Populate tuple data
                for (Integer columnIndex : this.columnPositionsToNames.keySet()) {
                    newTuple.add(columnIndex,this.columnPositionsToNames.get(columnIndex),resultSet.getObject(columnIndex));
                }
                this.tuples.add(newTuple);
            }
        } catch (Exception e) {
            // TODO
        }
    }

    @Override
    public boolean hasNext() {
        return this.currentPosition != (this.tuples.size()-1);
    }

    @Override
    public Tuple next() {
        return this.tuples.get(currentPosition++);
    }

    /**
     * Gets the number of Tuples (Rows) present within the given {@link QueryResult} object
     * @return the number of Tuples as an Integer
     */
    public int getTuplesCount() {
        return this.tuples.size();
    }

    /**
     * Sets the current row position for iteration. Tuple (Row) 1 is indexed at position 0, Row 2 is 1, .... Row X is X-1
     * @param position the position to set
     * @return true if a valid position was set, false otherwise (position before call will remain the same)
     */
    public boolean setPosition(int position) {
        if (position >= 0 && position < this.tuples.size()) {
            this.currentPosition = position;
            return true;
        }
        return false;
    }

}
