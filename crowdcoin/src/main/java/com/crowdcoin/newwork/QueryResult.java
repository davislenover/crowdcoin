package com.crowdcoin.newwork;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A translation class between a {@link java.sql.ResultSet} and a {@link javafx.scene.control.TableView}
 */
public class QueryResult {
    private Map<Integer,String> columnPositionsToNames;
    private List<Tuple> tuples;

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
                    newTuple.add(columnIndex,resultSet.getObject(columnIndex));
                }
                this.tuples.add(newTuple);
            }
        } catch (Exception e) {
            // TODO
        }
    }

    /**
     * Tuple object representing a single tuple (row) from a {@link ResultSet}
     */
    private static class Tuple {

        private int tupleNumber;

        private Map<Integer,Object> tupleValues;

        public Tuple(int tupleNumber) {
            this.tupleNumber = tupleNumber;
            this.tupleValues = new HashMap<>();
        }

        /**
         * Adds the value of a specific column for a given tuple (row)
         * @param columnIndex the corresponding column
         * @param value the data for the given column for the given tuple (row)
         */
        public void add(Integer columnIndex, Object value) {
            this.tupleValues.put(columnIndex,value);
        }

        /**
         * Gets the data corresponding to the columnIndex
         * @param columnIndex the given column for the given tuple (row) to retrieve data from
         * @return the corresponding data as an {@link Object}, null if columnIndex does not exist within the {@link Tuple} object
         */
        public Object get(Integer columnIndex) {
            if (this.tupleValues.containsKey(columnIndex)) {
                return this.tupleValues.get(columnIndex);
            }
            return null;
        }

        /**
         * Get tuple (row) number
         * @return the tuple (row) number as an Integer
         */
        public int getTupleNumber() {
            return this.tupleNumber;
        }


    }

}
