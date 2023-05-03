package com.crowdcoin.mainBoard;

import com.crowdcoin.mainBoard.table.CoinModel;
import com.crowdcoin.mainBoard.table.ModelClass;
import com.crowdcoin.mainBoard.table.ModelClassFactory;
import com.crowdcoin.mainBoard.table.Tab;
import com.crowdcoin.mainBoard.table.TableInformation;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.crowdcoin.mainBoard.table.Tab;

import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.List;

public class MainBoardController {

    // Toolbar menu button declarations

    // File
    @FXML private MenuButton fileMenuButton;
    @FXML private MenuItem fileMenuExit;

    // Edit
    @FXML private MenuButton editMenuButton;
    @FXML private MenuItem editMenuNewEntry;

    @FXML private TableView mainTable;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        CoinModel model = new CoinModel("myCompany","01234",12,"01/01/2002","$101.93","myDenomination","myGrade");
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata");
        Tab testTab = new Tab(model,table);
        testTab.loadTab(mainTable);

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
