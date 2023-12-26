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
        this.currentStartIndex = 0;
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
     * @throws InvalidRangeException
     */
    public List<Tuple> getRows(int startingIndex, int endingIndex) throws InvalidRangeException {
        if (startingIndex > endingIndex) {
            throw new InvalidRangeException(String.valueOf(startingIndex),String.valueOf(endingIndex));
        }

        List<Tuple> tuples = new ArrayList<>();
        // TODO, this will fail if starting index is larger than that of the actual result size
        queryResult.setPosition(startingIndex);

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
            // Check if incrementing the end index will go over the number of tuples
            if (this.currentEndIndex + this.numberOfRowsPerSet > (this.tupleCount-1)) {
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

    @Override
    public boolean hasNext() {
        return this.currentEndIndex != this.tupleCount;
    }

    @Override
    public List<Tuple> next() {
        List<Tuple> nextSet = this.getCurrentSet();
        this.setCurrentRowNext();
        return nextSet;
    }

    public List<Tuple> previous() {
        this.setCurrentRowPrevious();
        return this.getCurrentSet();
    }

}
