package com.crowdcoin.newwork;

import com.crowdcoin.exceptions.table.InvalidRangeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueryResultReader implements Iterator<List<Tuple>> {
    private QueryResult queryResult;
    private int tupleCount;
    private int currentStartIndex;
    private int currentEndIndex;
    private int numberOfRowsPerSet;
    private int lastDifference;

    public QueryResultReader(QueryResult queryResult, int numberOfRowsPerSet) {

        if (numberOfRowsPerSet <= 0) {
            throw new IllegalArgumentException("numberOfRowsPerSet cannot be less than or equal to 0");
        }

        this.queryResult = queryResult;
        this.currentStartIndex = -1;
        this.numberOfRowsPerSet = numberOfRowsPerSet;

        this.tupleCount = this.queryResult.getTuplesCount();

        this.currentEndIndex = Math.min(this.tupleCount-1, numberOfRowsPerSet-1);
        this.lastDifference = 0;

    }

    /**
     * Get a set of {@link Tuple} from a {@link QueryResult}
     * @param startingIndex the given Tuple starting index (inclusive)
     * @param endingIndex the given Tuple ending index (inclusive)
     * @return an unmodifiable list of {@link Tuple}
     * @throws InvalidRangeException if the startingIndex is greater than endingIndex. If startingIndex is greater than the largest {@link Tuple} index from {@link QueryResult}
     */
    public List<Tuple> getRows(int startingIndex, int endingIndex) throws InvalidRangeException {
        if (startingIndex > endingIndex) {
            throw new InvalidRangeException(String.valueOf(startingIndex),String.valueOf(endingIndex));
        }

        List<Tuple> tuples = new ArrayList<>();
        if (!queryResult.setPosition(startingIndex)) {
            throw new InvalidRangeException(String.valueOf(startingIndex),String.valueOf(endingIndex));
        }

        int size = endingIndex - startingIndex;
        int current = 0;

        while (current <= size && queryResult.hasNext()) {
            tuples.add(queryResult.next());
            current++;
        }

        return Collections.unmodifiableList(tuples);

    }

    // Method to increment a reader by a set
    private void setCurrentRowNext() {
        if (this.hasNext()) {

            // Iterator starts before the list, thus when at this "before" position, move to the first index to read
            if (this.currentStartIndex == -1) {
                this.currentStartIndex = 0;
            }

            // Check if incrementing the end index will go over the number of tuples
            else if (this.currentEndIndex + this.numberOfRowsPerSet > (this.tupleCount-1)) {
                // If so, get the difference and set the end index to the tuple count
                this.lastDifference = (this.currentEndIndex + this.numberOfRowsPerSet) - (this.tupleCount-1);
                this.currentEndIndex = this.tupleCount-1;
                // For the start index, increment by the number of rows and then remove whatever difference
                this.currentStartIndex += this.numberOfRowsPerSet - this.lastDifference;
            } else {
                this.currentEndIndex += this.numberOfRowsPerSet;
                this.currentStartIndex += this.numberOfRowsPerSet;
            }
        }
    }

    // Method to decrement reader by a set
    private void setCurrentRowPrevious() {
        // Check if the end has been reached
        if (!this.hasNext()) {
            // Thus, need to know the last difference
            int moveBack = this.numberOfRowsPerSet - this.lastDifference;

            // Check if decrementing will cause start index to be less than 1
            if (this.currentStartIndex - moveBack < 0) {
                // The reader is then moving back to the start
                this.currentStartIndex = 0;
                this.currentEndIndex = this.numberOfRowsPerSet;
            } else {
                // Otherwise, just decrement
                this.currentEndIndex -= moveBack;
                this.currentStartIndex -= moveBack;
            }
            this.lastDifference = 0;
        } else {
            if (this.currentStartIndex - this.numberOfRowsPerSet < 0) {
                this.currentStartIndex = 0;
                this.currentEndIndex = this.numberOfRowsPerSet;
            } else {
                this.currentEndIndex -= this.numberOfRowsPerSet;
                this.currentStartIndex -= this.numberOfRowsPerSet;
            }
        }
    }

    private List<Tuple> getCurrentSet() {
        List<Tuple> returnList = new ArrayList<>();
        this.queryResult.setPosition(this.currentStartIndex);
        for (int index = this.currentStartIndex; index <= this.currentEndIndex; index++) {
            returnList.add(this.queryResult.next());
        }
        return returnList;
    }

    /**
     * Checks if the row set pointer is currently at the first row set (NOT before or after)
     * @return true if the row set pointer is currently at the first row set, false otherwise
     */
    public boolean isAtStart() {
        return this.currentStartIndex == 0;
    }

    @Override
    public boolean hasNext() {
        return (this.currentEndIndex != (this.tupleCount-1));
    }

    /**
     * Sets the row set pointer to the next row set (if applicable) and returns said row set.
     * @return The next row set (a {@link List} of {@link Tuple}s). If call is made while the row set pointer is at the last row set, the row set pointer will stay as is and thus, the last row set will be returned
     */
    @Override
    public List<Tuple> next() {
        this.setCurrentRowNext();
        return this.getCurrentSet();
    }

    /**
     * Gets the previous row set and sets the row set pointer back to said previous row set (if applicable).
     * @return The previous row set (a {@link List} of {@link Tuple}s). If call is made while current result set pointer is before the first set, the pointer will be
     * incremented to the first set and thus, the first set will be returned. Any calls while the pointer is at the first row set will return the first row set
     */
    public List<Tuple> previous() {
        this.setCurrentRowPrevious();
        return this.getCurrentSet();
    }

}
