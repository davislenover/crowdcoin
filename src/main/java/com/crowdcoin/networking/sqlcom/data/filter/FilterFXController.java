package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.format.defaultActions.filter.NewFilterPopWindow;
import com.crowdcoin.mainBoard.table.Tab;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;

/**
 * A class responsible for loading filters into SplitMenuButton objects
 */
public class FilterFXController {

    /**
     * Apply filters to a given SplitMenuButton
     * @param filterButton the target SplitMenuButton
     * @param filterManager the FilterManager object to get the Filter objects from
     */
    public void applyFilters(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table, Tab tabInvoking) {
        filterButton.getItems().clear();
        filterButton.getItems().addAll(filterManager.getFilterNodes());

        MenuItem newFilter = new MenuItem();
        newFilter.setText("New Filter...");
        newFilter.setOnAction(new NewFilterPopWindow(filterButton,filterManager,table));
        filterButton.getItems().add(newFilter);

    }
}
