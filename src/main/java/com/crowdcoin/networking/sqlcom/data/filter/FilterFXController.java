package com.crowdcoin.networking.sqlcom.data.filter;

import javafx.scene.control.SplitMenuButton;

/**
 * A class responsible for loading filters into SplitMenuButton objects
 */
public class FilterFXController {

    /**
     * Apply filters to a given SplitMenuButton
     * @param filterButton the target SplitMenuButton
     * @param filterManager the FilterManager object to get the Filter objects from
     */
    public void applyFilters(SplitMenuButton filterButton, FilterManager filterManager) {
        filterButton.getItems().clear();
        filterButton.getItems().addAll(filterManager.getFilterNodes());
    }
}
