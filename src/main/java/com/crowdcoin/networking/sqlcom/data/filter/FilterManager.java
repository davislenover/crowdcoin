package com.crowdcoin.networking.sqlcom.data.filter;

import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class FilterManager implements Collection<Filter> {

    private List<Filter> filterList;

    public FilterManager() {
        this.filterList = new ArrayList<>();
    }

    @Override
    public int size() {
        return this.filterList.size();
    }

    @Override
    public boolean isEmpty() {
        return this.filterList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        try {
            // This will call override equals() method for each filter
            return this.filterList.contains((Filter) o);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Iterator<Filter> iterator() {
        return new Iterator<Filter>() {
            @Override
            public boolean hasNext() {
                return filterList.iterator().hasNext();
            }

            @Override
            public Filter next() {
                return filterList.iterator().next();
            }
        };
    }

    @Override
    public Object[] toArray() {
        return filterList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return (T[]) filterList.toArray();
    }

    /**
     * Add a Filter to the given FilterManager. Filter must not be the same as any Filter found within the FilterManager
     * @param filter element whose presence in this collection is to be ensured
     * @return true if the Filter was added. False otherwise
     */
    @Override
    public boolean add(Filter filter) {
        if (this.filterList.contains(filter)) {
            return false;
        } else {
            return this.filterList.add(filter);
        }
    }

    @Override
    public boolean remove(Object o) {
        if (this.filterList.contains(o)) {
            return this.filterList.remove(o);
        } else {
            return false;
        }
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return this.filterList.containsAll(c);
    }

    /**
     * Add a collection of Filters to the given FilterManager. If any Filter is already present within the FilterManager, no Filters will be added
     * @param c collection containing elements to be added to this collection
     * @return true if all Filters were added, false if none were added
     */
    @Override
    public boolean addAll(Collection<? extends Filter> c) {

        for (Filter filterToAdd : c) {
            if (this.filterList.contains(filterToAdd)) {
                return false;
            }
        }

        return this.filterList.addAll(c);
    }

    /**
     * Remove a collection of Filters from the given FilterManager. If any Filter is not already present within the FilterManager, no Filters will be removed
     * @param c collection containing elements to be removed from this collection
     * @return true if all Filters were removed, false if none were removed
     */
    @Override
    public boolean removeAll(Collection<?> c) {

        for (Object filterToRemove : c) {
            if (!this.filterList.contains(filterToRemove)) {
                return false;
            }
        }

        return this.filterList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return this.filterList.retainAll(c);
    }

    @Override
    public void clear() {
        this.filterList.clear();
    }

    /**
     * Get combined WHERE query of all filters within the collection with AND statements.
     * @return the combined WHERE query SQL statement as a string, starting with a space character. If no filters exist within the collection, an empty String object is returned
     */
    public String getCombinedQuery() {


        int size = this.filterList.size();

        if (size == 0) {
            return "";
        }

        String returnQuery = " WHERE";

        // Loop through each filter in list
        for (int index = 0; index < size; index++) {

            // Check if getting last filter (to not add " AND" at the end)
            if (index == (size - 1)) {

                returnQuery += this.filterList.get(index).getBareString();

            } else {

                returnQuery += this.filterList.get(index).getBareString();
                returnQuery += " AND";

            }

        }

        return returnQuery;

    }

    /**
     * Gets a list of MenuItem objects representing each filter within the FilterManager object
     * @return a list of MenuItem objects
     */
    public List<MenuItem> getFilterNodes() {

        List<MenuItem> returnList = new ArrayList<>();

        for (Filter filter : this.filterList) {

            MenuItem filterMenuItem = new MenuItem();
            filterMenuItem.setText(filter.getTargetColumnName());

            // TODO Set filterAction

            returnList.add(filterMenuItem);

        }

        return returnList;

    }


}
