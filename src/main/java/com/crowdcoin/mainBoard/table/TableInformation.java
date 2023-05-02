package com.crowdcoin.mainBoard.table;
import javafx.scene.control.TableColumn;
import java.util.ArrayList;
import java.util.List;

// Class contains table columns and associated data
public class TableInformation {

    // Columns can be any data type as long as the data in question is comparable
    // Thus one can implement logical comparisons later on
    private List<TableColumn<? extends TableReadable<?>,? extends Comparable<?>>> columnData;

    public TableInformation() {
        this.columnData = new ArrayList<>();
    }

    public <T extends Comparable<T>> boolean addColumn(TableColumn<TableReadable<T>, T> column) {
        // Add to list
        return this.columnData.add(column);
    }

    public boolean removeColumn(String columnName) {

        return removeColumn(getColumn(columnName));

    }

    public <T extends Comparable<T>> boolean removeColumn(TableColumn<TableReadable<T>, T> column) {
        // Remove column from list (if applicable)
        return this.columnData.remove(column);
    }

    public TableColumn getColumn(String columnName) {

        for (TableColumn currentColumn : this.columnData) {

            if (currentColumn.getId().equals(columnName)) {

                return currentColumn;

            }

        }

        return null;

    }

}
