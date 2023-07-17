package com.crowdcoin.mainBoard;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.toolBar.MenuGroup;
import com.crowdcoin.mainBoard.toolBar.MenuGroupContainer;
import com.crowdcoin.mainBoard.toolBar.MenuOption;
import com.crowdcoin.mainBoard.window.ExportTabPopWindow;
import com.crowdcoin.mainBoard.window.NewEntryPopWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.crowdcoin.mainBoard.table.Tab;
import javafx.scene.layout.*;

public class MainBoardController {

    // Main Node Declarations

    @FXML private TableView mainTable;
    @FXML private GridPane rightDisplay;
    @FXML private GridPane buttonGrid;
    @FXML private ToolBar toolBar;
    @FXML private TabPane tabBar;
    @FXML private Button prevTableViewButton;
    @FXML private Button nextTableViewButton;
    @FXML private SplitMenuButton filters;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        CoinModel model = new CoinModel(12,"myDenomination","01/01/2002","myGrade","myCompany","01234","$101.93");
        ModelClass modelClass = new ModelClassFactory().build(model);
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());
        SQLTable table2 = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());

        // Test Tab 1
        Tab testTab = new Tab(model,table,"testTab");
        testTab.setTabTableAction(new ViewTableRowEvent());
        // Test Tab 2
        Tab testTab2 = new Tab(model,table2,"testTab2");
        testTab2.setTabTableAction(new ViewTableRowEvent());

        TabBar testBar = new TabBar(tabBar,mainTable,rightDisplay,buttonGrid,prevTableViewButton,nextTableViewButton,filters);
        testBar.addTab(testTab);
        testBar.addTab(testTab2);

        // Test adding MenuOptions
        MenuGroupContainer testContainer = new MenuGroupContainer(this.toolBar);

        MenuGroup test1 = new MenuGroup("Test1");
        test1.addOption(new MenuOption("Exit", p -> Platform.exit()));
        test1.addOption(new MenuOption("Hello", p -> System.out.println("Hello world!")));
        test1.addOption(new MenuOption("New Tab Test", p -> testBar.addTab(testTab)));
        test1.addOption(new MenuOption("New Entry",p-> {
            try {
                PopWindow newEntry = new NewEntryPopWindow("New Entry",table);
                newEntry.start(StageManager.getStage(newEntry));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        MenuGroup test2 = new MenuGroup("Test2");

        // Note that options are cloned thus updates need to be made before adding to container (this might change in the future)
        test1.removeOption(new MenuOption("Hello",p -> System.out.println("Test!")));

        testContainer.addMenuGroup(test1);
        testContainer.addMenuGroup(test2);

    }
}
