package com.crowdcoin.networking.sqlcom.data.filter;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.window.EditFilterPopWindow;
import com.crowdcoin.mainBoard.window.NewFilterPopWindow;
import com.crowdcoin.mainBoard.table.Observable;
import com.crowdcoin.mainBoard.table.Observer;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
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

        // Get all nodes
        HashMap<MenuItem,Filter> filterNodes = filterManager.getFilterNodes();
        // FilterController will observe if any nodes are clicked on
        for (MenuItem filterNode : filterNodes.keySet()) {
            // OnAction create an edit filter window and pass corresponding arguments
            filterNode.setOnAction(event -> {
                try {
                    PopWindow editFilterWindow = new EditFilterPopWindow(filterNodes.get(filterNode), filterManager,table,this);
                    editFilterWindow.start(StageManager.getStage(editFilterWindow));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        // Add MenuItems to SplitMenuButton
        filterButton.getItems().addAll(filterNodes.keySet());

        MenuItem newFilter = new MenuItem();
        newFilter.setText("New Filter...");
        newFilter.setOnAction(event -> {
            try {
                // Create a new FilterPopWindow and display on screen (start)
                PopWindow newFilterWindow = new NewFilterPopWindow(filterButton,filterManager,table,this);
                newFilterWindow.start(StageManager.getStage(newFilterWindow));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
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
