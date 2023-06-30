package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.format.defaultActions.filter.NewFilterPopWindow;
import com.crowdcoin.mainBoard.table.Observable;
import com.crowdcoin.mainBoard.table.Observer;
import com.crowdcoin.mainBoard.table.Tab;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for loading filters into SplitMenuButton objects
 */
public class FilterFXController implements Observable<FilterFXController> {

    private List<Observer<FilterFXController>> subscriptionList;

    public FilterFXController() {
        this.subscriptionList = new ArrayList<>();
    }

    /**
     * Apply filters to a given SplitMenuButton
     * @param filterButton the target SplitMenuButton
     * @param filterManager the FilterManager object to get the Filter objects from
     */
    public void applyFilters(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table) {
        filterButton.getItems().clear();
        filterButton.getItems().addAll(filterManager.getFilterNodes());

        MenuItem newFilter = new MenuItem();
        newFilter.setText("New Filter...");
        newFilter.setOnAction(new NewFilterPopWindow(filterButton,filterManager,table,this));
        filterButton.getItems().add(newFilter);

    }

    @Override
    public boolean addObserver(Observer<FilterFXController> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<FilterFXController> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer<FilterFXController> observer : this.subscriptionList) {
            observer.update(this);
        }
    }
}
