package com.crowdcoin.mainBoard;

import com.crowdcoin.mainBoard.Interactive.TextFieldCombo;
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
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import com.crowdcoin.mainBoard.table.Tab;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

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
    @FXML private GridPane rightDisplay;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        CoinModel model = new CoinModel("myCompany","01234",12,"01/01/2002","$101.93","myDenomination","myGrade");
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata");
        Tab testTab = new Tab(model,table);
        testTab.loadTab(mainTable);

        TextFieldCombo testCombo = new TextFieldCombo("This is a test", "This is a test combo object for textfield combo's");
        TextFieldCombo testCombo2 = new TextFieldCombo("This is a test2", "This is a test combo object for textfield combo's 2");
        TextFieldCombo testCombo3 = new TextFieldCombo("This is a test3", "This is a test combo object for textfield combo's 3");
        TextFieldCombo testCombo4 = new TextFieldCombo("This is a test4", "This is a test combo object for textfield combo's 4");

        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setHalignment(HPos.CENTER);
        rightDisplay.getColumnConstraints().add(constraints);

        testCombo.setupForPane(rightDisplay);
        testCombo2.setupForPane(rightDisplay);
        testCombo3.setupForPane(rightDisplay);
        testCombo4.setupForPane(rightDisplay);

        rightDisplay.add(testCombo.getPane(),0,0);
        rightDisplay.add(testCombo2.getPane(),0,1);
        rightDisplay.add(testCombo3.getPane(),0,2);
        rightDisplay.add(testCombo4.getPane(),0,3);



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
