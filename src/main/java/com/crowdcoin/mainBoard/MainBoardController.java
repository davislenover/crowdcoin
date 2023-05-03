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

        // Basically, each table column doesn't know how to get data to display it other than that it is using a ModelClass type as input and Object type as output
        // How to get data is specified by setting the cell value factory of each column, where in this case, it is set to get the ModelClass object and invoke the corresponding method
        // Note that it is NOT a specific ModelClass, it simply specifies that once it receives a specific instance, it will do what it was told to do with any other ModelClass
        // The corresponding method is at the same index as the current size of the column data list (at the time) thus if a ModelClass has 4 invokable methods, the first column will get data from method 1
        // second from method 2 and so on
        // Thus when we add a ModelClass here, to display the data we are simply invoking the corresponding method within the specific instance of the ModelClass at the given index

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
