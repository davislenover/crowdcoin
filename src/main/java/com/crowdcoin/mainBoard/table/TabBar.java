package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TabBar implements Observer<Tab> {

    private TabPane controlBar;
    private Map<String,Tab> tabIDMap;
    private TableView mainTable;
    private GridPane fieldGrid;
    private GridPane buttonGrid;
    private Button previous;
    private Button next;

    private SplitMenuButton filterButton;

    /**
     * Creates a TabBar. Manages Tabs on screen
     * @param controlTabPane the JavaFX TabPane object to control. This is where users will be able to select and delete Tabs
     * @param mainTable the JavaFX TableView object to control (for displaying SQL content). Users can select rows within this TableView which will cause arbitrary logic to execute (most likely to update field and button panes)
     * @param fieldGrid the JavaFX GridPane object to control for fields. This GridPane houses user interactable TextFields and is where Tabs will load their InteractiveTextFields into
     * @param buttonGrid the JavaFX GridPane object to control for buttons. This GridPane houses user interactable Buttons and is where Tabs will load their InteractiveButtons into
     */
    public TabBar(TabPane controlTabPane, TableView mainTable, GridPane fieldGrid, GridPane buttonGrid, Button previous, Button next, SplitMenuButton filterButton) {

        this.controlBar = controlTabPane;
        this.controlBar.getTabs().clear();
        this.controlBar.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        this.mainTable = mainTable;
        this.fieldGrid = fieldGrid;
        this.buttonGrid = buttonGrid;
        this.previous = previous;
        this.next = next;
        this.filterButton = filterButton;

        this.tabIDMap = new HashMap<>();

    }

    /**
     * Add a Tab to the TabBar. If added, a new Tab is created in the TabPane and automatically selected in which the content from the given Tab is loaded. The tabID must be unique
     * @param tab the Tab (non JavaFX) to add
     * @return true if the Tab was added, false otherwise (if false, the tabID was likely not unique and already exists within this TabBar)
     */
    public boolean addTab(Tab tab) {

        String tabID = tab.getTabID();

        // Make sure tabID does not already exist
        if (this.tabIDMap.containsKey(tabID)) {
            return false;
        }

        // If not, add the ID string and the corresponding tab to the HashMap
        this.tabIDMap.put(tabID,tab);

        // Create a new JavaFX Tab
        javafx.scene.control.Tab javaFXTab = new javafx.scene.control.Tab(tabID);
        javaFXTab.setClosable(true);
        // Set the tab to have the same ID as the data Tab
        javaFXTab.setId(tabID);

        // Set methods to invoke on events
        javaFXTab.setOnClosed(p -> this.closeTab(javaFXTab.getId()));
        // Note selection changed means the current tab was selected but then the user selected another tab
        javaFXTab.setOnSelectionChanged(p -> {
            try {
                this.openTab(javaFXTab.getId());
            } catch (Exception e) {
                // TODO add exception handling
            }
        });

        // Get the TabPane and add the new Tab
        this.controlBar.getTabs().add(javaFXTab);
        // Set the new Tab to be currently selected in the TabPane
        // Note this will invoke selectionChanged event
        this.controlBar.getSelectionModel().select(javaFXTab);

        // Add TabBar to Tabs observer subscription list
        tab.addObserver(this);

        return true;

    }

    public boolean removeTab(String tabID) {

        if (this.tabIDMap.containsKey(tabID)) {
            // Remove TabBar from tab observer subscription list
            this.tabIDMap.get(tabID).removeObserver(this);
            this.tabIDMap.remove(tabID);
            return true;

        } else {
            return false;
        }

    }

    private void closeTab(String tabID) {

        removeTab(tabID);
        if (this.tabIDMap.isEmpty()) {
            clearScreen();
        }

    }

    // Method to load data Tab to screen
    // This method is invoked from OnSelectionChanged event from created JavaFX Tab
    private void openTab(String tabID) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Find the JavaFX tab that was selected
        javafx.scene.control.Tab newlySelectedTab = this.controlBar.getSelectionModel().getSelectedItem();

        // Get the corresponding data Tab by using the JavaFX Tab ID
        Tab tabToLoad = this.tabIDMap.get(newlySelectedTab.getId());

        // Load the corresponding data Tab
        tabToLoad.loadTab(this.mainTable,this.fieldGrid,this.buttonGrid,this.previous,this.next,this.filterButton);

    }

    // Method to clear the screen
    // Called when no more tabs are present in the TabPane
    private void clearScreen() {

        this.mainTable.getItems().clear();
        this.mainTable.getColumns().clear();
        this.fieldGrid.getChildren().clear();
        this.buttonGrid.getChildren().clear();
        this.previous.setDisable(true);
        this.next.setDisable(true);
        this.filterButton.setDisable(true);

        this.mainTable.setOnMouseClicked(null);

    }

    @Override
    public void update(Tab passThroughObject) {

    }
}
