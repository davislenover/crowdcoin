package com.crowdcoin.mainBoard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;

public class MainBoardController {

    // Toolbar menu button declarations

    // File
    @FXML private MenuButton fileMenuButton;
    @FXML private MenuItem fileMenuExit;

    // Edit
    @FXML private MenuButton editMenuButton;
    @FXML private MenuItem editMenuNewEntry;

    @FXML private ListView<String> mainCoinList;

    // Method to initialize coin list on startup
    public void initializeList() {

    }

    // Events

    // Exit Button Action
    @FXML
    protected void onMenuItemExitAction() {

        Platform.exit();

    }

    // New Entry into database
    @FXML
    protected void onNewEntry() {




    }


}
