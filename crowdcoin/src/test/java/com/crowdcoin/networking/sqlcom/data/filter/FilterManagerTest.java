package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.networking.sqlcom.data.filter.filterOperators.GeneralFilterOperators;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterManagerTest {

    // Test for correct combined query string
    @Test
    public void testFilterString() {

        // Collection of Strings (as objects) for In/Not In filters
        List<Object> testStrings = new ArrayList<>() {{
            add("test1");
            add("test2");
            add("test3");
        }};

        List<Object> testStrings2 = new ArrayList<>() {{
            add("test4");
            add("test5");
        }};

        // Create filter objects
        Filter filter1 = new BetweenFilter("betweenTestColumn", new ArrayList<>() {{
            add(1);
            add(5);
        }});
        Filter filter2 = new InFilter("InFilterTestColumn",testStrings);
        Filter filter3 = new NotInFilter("NotInFilterTestColumn",testStrings2);
        Filter filter4 = new GeneralFilter("generalColumn", GeneralFilterOperators.GREATERTHAN,3);
        Filter filter5 = new GeneralFilter("generalColumn2",GeneralFilterOperators.EQUAL, "hello");

        // Add all to filter manager
        FilterManager filterManager = new FilterManager() {{
            add(filter1);
            add(filter2);
            add(filter3);
            add(filter4);
            add(filter5);
        }};

        assertEquals(" WHERE betweenTestColumn BETWEEN '1' AND '5' AND InFilterTestColumn IN ('test1', 'test2', 'test3') AND NotInFilterTestColumn NOT IN ('test4', 'test5') AND generalColumn > '3' AND generalColumn2 = 'hello'",filterManager.getCombinedQuery());

    }

    // Test if identical filters cannot be added to filter manager
    @Test
    public void testDuplicateFilter() {

        FilterManager filterManager = new FilterManager();

        Filter testFilter = new GeneralFilter("testName",GeneralFilterOperators.EQUAL,3);
        Filter testFilter2 = new GeneralFilter("testName",GeneralFilterOperators.EQUAL,3);
        Filter testFilter3 = new GeneralFilter("testName",GeneralFilterOperators.GREATERTHAN,3);

        filterManager.add(testFilter);
        filterManager.add(testFilter3);
        // False indicates filter already exists in manager
        assertEquals(false,filterManager.add(testFilter2));

        assertEquals(2,filterManager.size());
        assertEquals(true,filterManager.contains(testFilter));
        assertEquals(true,filterManager.contains(testFilter3));

    }


    // Test removing filters
    @Test
    public void testRemovalOfFilter() {

        FilterManager filterManager = new FilterManager();

        Filter testFilter = new GeneralFilter("testName",GeneralFilterOperators.EQUAL,3);
        Filter testFilter3 = new GeneralFilter("testName",GeneralFilterOperators.GREATERTHAN,3);
        filterManager.add(testFilter);
        filterManager.add(testFilter3);
        assertEquals(2,filterManager.size());

        filterManager.remove(testFilter3);
        assertEquals(1,filterManager.size());
        assertEquals(true,filterManager.contains(testFilter));
        assertEquals(false,filterManager.contains(testFilter3));

    }

}