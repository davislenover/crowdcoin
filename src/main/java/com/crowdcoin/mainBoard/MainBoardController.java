package com.crowdcoin.mainBoard;

import com.crowdcoin.mainBoard.Interactive.InteractiveButtonActionEvent;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.toolBar.MenuGroup;
import com.crowdcoin.mainBoard.toolBar.MenuGroupContainer;
import com.crowdcoin.mainBoard.toolBar.MenuOption;
import com.crowdcoin.mainBoard.toolBar.MenuOptionActionEvent;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.crowdcoin.mainBoard.table.Tab;
import javafx.scene.layout.*;

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
    @FXML private GridPane buttonGrid;
    @FXML private ToolBar toolBar;

    // Method to initialize coin list on startup
    public void initializeList() throws Exception {

        CoinModel model = new CoinModel(12,"myDenomination","01/01/2002","myGrade","myCompany","01234","$101.93");
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata");

        Tab testTab = new Tab(model,table);
        testTab.setTabTableAction(new TabActionEvent() {
            @Override
            public void tableActionHandler(ColumnContainer columnContainer, InteractivePane pane) {
                System.out.println("This tab was pressed!");
            }
        });

        // an InteractivePane houses all data for a specific tab in regard to the rightDisplay and buttonGrid
        InteractivePane testPane = testTab.getInteractivePane();
        // Add fields to the pane
        testPane.addField("This is a test", "This is a test combo object for textfield combo's 1");
        testPane.addField("This is a test2", "This is a test combo object for textfield combo's 2");
        testPane.addField("This is a test3", "This is a test combo object for textfield combo's 3");

        // Testing buttons
        // One can specify how they would like a button to handle an ActionEvent by defining it within a specific class or on the fly
        InteractiveButtonActionEvent testHandler = new InteractiveButtonActionEvent() {
            @Override
            public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
                System.out.println(event.getEventType().getName());
                System.out.println(button.getText() + " fired an event!");
            }
        };

        testPane.addButton("Button1",testHandler);
        testPane.addButton("Button2", testHandler);

        testTab.loadTab(mainTable,rightDisplay,buttonGrid);

        // Test removing elements
        testPane.removeButton(0);
        testPane.removeField(0);
        testTab.loadTab(mainTable,rightDisplay,buttonGrid);

        // Test adding MenuOptions
        MenuGroupContainer testContainer = new MenuGroupContainer(this.toolBar);

        MenuGroup test1 = new MenuGroup("Test1");
        test1.addOption(new MenuOption("Exit", p -> Platform.exit()));
        test1.addOption(new MenuOption("Hello", p -> System.out.println("Hello world!")));
        MenuGroup test2 = new MenuGroup("Test2");

        // Note that options are cloned thus updates need to be made before adding to container (this might change in the future)
        test1.removeOption(new MenuOption("Hello",p -> System.out.println("Test!")));

        testContainer.addMenuGroup(test1);
        testContainer.addMenuGroup(test2);

        testContainer.removeMenuGroup(test2);


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
