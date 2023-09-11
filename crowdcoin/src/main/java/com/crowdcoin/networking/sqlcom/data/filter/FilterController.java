package com.crowdcoin.networking.sqlcom.data.filter;

import com.ratchet.window.StageManager;
import com.ratchet.window.WindowManager;
import com.ratchet.observe.ModifyEvent;
import com.crowdcoin.mainBoard.window.EditFilterPopWindow;
import com.crowdcoin.mainBoard.window.NewFilterPopWindow;
import com.ratchet.observe.Observable;
import com.ratchet.observe.Observer;
import com.ratchet.window.PopWindow;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class responsible for loading filters into SplitMenuButton objects
 */
public class FilterController implements Observable<ModifyEvent,String> {

    private List<Observer<ModifyEvent,String>> subscriptionList;

    public FilterController() {
        this.subscriptionList = new ArrayList<>();
    }

    /**
     * Apply filters to a given SplitMenuButton
     * @param filterButton the target SplitMenuButton
     * @param filterManager the FilterManager object to get the Filter objects from
     */
    public void applyFilters(SplitMenuButton filterButton, FilterManager filterManager, SQLTable table, WindowManager manager) {
        filterButton.getItems().clear();

        // Get all nodes
        HashMap<MenuItem,Filter> filterNodes = filterManager.getFilterNodes();
        // FilterController will observe if any nodes are clicked on
        for (MenuItem filterNode : filterNodes.keySet()) {
            // OnAction create an edit filter window and pass corresponding arguments
            filterNode.setOnAction(event -> {
                try {
                    PopWindow editFilterWindow = new EditFilterPopWindow(filterNodes.get(filterNode), filterManager,table,this,manager);
                    editFilterWindow.setId("Edit Filter");
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
                PopWindow newFilterWindow = new NewFilterPopWindow(filterButton,filterManager,table,this,manager);
                newFilterWindow.setId("New Filter");
                newFilterWindow.start(StageManager.getStage(newFilterWindow));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        filterButton.getItems().add(newFilter);

        filterButton.setText("Filters - " + filterButton.getItems().size());

    }

    @Override
    public boolean addObserver(Observer<ModifyEvent,String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ModifyEvent,String> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(ModifyEvent event) {
        for (Observer<ModifyEvent,String> observer : List.copyOf(this.subscriptionList)) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }
}
