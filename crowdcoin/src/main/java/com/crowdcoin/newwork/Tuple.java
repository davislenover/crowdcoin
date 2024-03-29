package com.crowdcoin.newwork;

import com.crowdcoin.mainBoard.table.modelClass.TableReadable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Tuple object representing a single tuple (row) from a {@link QueryResult}
 */
public class Tuple {
    private int tupleNumber;

    private Map<Integer,String> columnPositionToName;
    private Map<Integer,Object> tupleValues;

    public Tuple(int tupleNumber) {
        this.tupleNumber = tupleNumber;
        this.tupleValues = new HashMap<>();
        this.columnPositionToName = new HashMap<>();
    }

    /**
     * Adds the value of a specific column for a given tuple (row)
     * @param columnIndex the corresponding column
     * @param value the data for the given column for the given tuple (row)
     */
    public void add(Integer columnIndex, String columnName, Object value) {
        this.tupleValues.put(columnIndex,value);
        this.columnPositionToName.put(columnIndex,columnName);
    }

    /**
     * Gets the data corresponding to the columnIndex
     * @param columnIndex the given column for the given tuple (row) to retrieve data from
     * @return the corresponding data as an {@link Object}, null if columnIndex does not exist within the {@link Tuple} object
     */
    @TableReadable(abstractGetDataMethod = true)
    public Object getData(Integer columnIndex) {
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

    /**
     * @return a {@link Map<Integer,String>} which stores the column index's with their corresponding names
     */
    @TableReadable(abstractGetColumnsMethod = true)
    public Map<Integer,String> getColumnPositionToName() {
        return Collections.unmodifiableMap(this.columnPositionToName);
    }

}
