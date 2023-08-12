package com.crowdcoin.mainBoard;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.table.tabActions.GradeRowEvent;
import com.crowdcoin.mainBoard.table.tabActions.ViewTableRowEvent;
import com.crowdcoin.mainBoard.table.tabActions.ViewUserRowEvent;
import com.crowdcoin.mainBoard.toolBar.MenuGroup;
import com.crowdcoin.mainBoard.toolBar.MenuGroupContainer;
import com.crowdcoin.mainBoard.toolBar.MenuOption;
import com.crowdcoin.mainBoard.window.AddUserPopWindow;
import com.crowdcoin.mainBoard.window.NewEntryPopWindow;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.SQLTableGroup;
import com.crowdcoin.networking.sqlcom.data.constraints.*;
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
    @FXML private Button exportTabButton;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        CoinModel model = new CoinModel(12,"myDenomination","01/01/2002","myGrade","myCompany","01234","$101.93");
        ModelClass modelClass = new ModelClassFactory().build(model);
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());
        SQLTable table2 = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());

        UserModel userModel = new UserModel(101,0.00,"test","test2");
        ModelClass modelClassUser = new ModelClassFactory().build(userModel);
        SQLTable table3 = new SQLTable(SQLData.getSqlConnection(),"userData", modelClassUser.getColumns());

        SQLColumnConstraint nameConstraint = new NameConstraint("USERID",SQLData.credentials.getUsername());
        SQLColumnConstraint nameConstraintIsGraded = new NameConstraint("IsGraded","null");
        SQLCellConstraint cellValueConstraint = new CellEqualsConstraint("0");
        SQLConstraintGroup group = new SQLConstraintGroup(SQLData.credentials.getUsername());
        group.add(cellValueConstraint);
        table3.getConstraints().addGroup(group);
        table3.getConstraints().add(nameConstraint);
        table3.getConstraints().add(nameConstraintIsGraded);

        Tab testWorkTab = new Tab(userModel,table3,"testWorkTab");
        testWorkTab.setTabTableAction(new GradeRowEvent(new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns())));

        // Test Tab 1
        Tab testTab = new Tab(model,table,"testTab");
        testTab.setTabTableAction(new ViewTableRowEvent(table3));
        // Test Tab 2
        Tab testTab2 = new Tab(model,table2,"testTab2");
        testTab2.setTabTableAction(new ViewTableRowEvent(table3));

        TabBar testBar = new TabBar(tabBar,mainTable,rightDisplay,buttonGrid,prevTableViewButton,nextTableViewButton,filters,exportTabButton);
        testBar.addTab(testTab);
        testBar.addTab(testTab2);
        testBar.addTab(testWorkTab);

        // Test adding MenuOptions
        MenuGroupContainer testContainer = new MenuGroupContainer(this.toolBar);

        MenuGroup test1 = new MenuGroup("Test1");
        test1.addOption(new MenuOption("Exit", p -> Platform.exit()));
        test1.addOption(new MenuOption("Hello", p -> System.out.println("Hello world!")));
        test1.addOption(new MenuOption("New Tab Test", p -> testBar.addTab(testTab)));
        test1.addOption(new MenuOption("New Entry",p-> {
            try {
                SQLTable tableNewEntry = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());
                tableNewEntry.getConstraints().add(new NameConstraint("grade","null"));
                PopWindow newEntry = new NewEntryPopWindow("New Entry",tableNewEntry,table3);
                newEntry.start(StageManager.getStage(newEntry));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        ModelClass modelClassUserNames = new ModelClassFactory().build(new permsModel("test"));
        SQLTable tableUserData = new SQLTable(SQLData.getSqlConnection(),"userData", modelClassUser.getColumns());
        SQLTable userNameTable = new SQLTable(SQLData.getSqlConnection(),"userGrantsData",modelClassUserNames.getColumns());
        MenuGroup test2 = new MenuGroup("Test2");
        test2.addOption(new MenuOption("Add new user",option -> {
            try {
                PopWindow newUser = new AddUserPopWindow("Add User",table3,userNameTable,modelClassUser);
                newUser.start(StageManager.getStage(newUser));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));

        Tab testUserTab = new Tab(new permsModel("test"),userNameTable,"testUserTab");
        testUserTab.setTabTableAction(new ViewUserRowEvent(tableUserData));

        test2.addOption(new MenuOption("Open Users Tab", option -> {
            testBar.addTab(testUserTab);
        }));

        // Note that options are cloned thus updates need to be made before adding to container (this might change in the future)
        test1.removeOption(new MenuOption("Hello",p -> System.out.println("Test!")));

        testContainer.addMenuGroup(test1);
        testContainer.addMenuGroup(test2);

    }
}
