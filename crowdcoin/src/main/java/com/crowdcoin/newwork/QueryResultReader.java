package com.crowdcoin.newwork;

import com.crowdcoin.exceptions.table.InvalidRangeException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class QueryResultReader implements Iterator<List<Tuple>> {
    private QueryResult queryResult;
    private int currentStartIndex;
    private int currentEndIndex;
    private int numberOfRows;

    public QueryResultReader(QueryResult queryResult, int numberOfRows) {
        this.queryResult = queryResult;
        this.currentStartIndex = 0;

        if (this.queryResult.getTuplesCount() > numberOfRows) {
            this.numberOfRows = this.queryResult.getTuplesCount();
        } else {
            this.numberOfRows = numberOfRows;
        }

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


    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public List<Tuple> next() {
        return null;
    }
}
