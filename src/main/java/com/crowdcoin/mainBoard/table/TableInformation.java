package com.crowdcoin.mainBoard.table;

import javafx.scene.control.TableColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

// Class contains table columns and associated data
public class TableInformation<T extends Object> {
    private List<TableColumn<TableReadable<T>,T>> columnData;

    public TableInformation() {
        this.columnData = new ArrayList<>();
    }

    public TableInformation(Set<String> columnNames) {

        this.columnData = new ArrayList<>();

        for (String columnName : columnNames) {

            // Create a new column with the given name
            TableColumn<TableReadable<T>, T> newColumn = new TableColumn<>(columnName);
            // Add to list
            this.columnData.add(newColumn);

        }

    }

}
