package com.crowdcoin.mainBoard;

import com.crowdcoin.mainBoard.Interactive.*;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.toolBar.MenuGroup;
import com.crowdcoin.mainBoard.toolBar.MenuGroupContainer;
import com.crowdcoin.mainBoard.toolBar.MenuOption;
import com.crowdcoin.mainBoard.window.PopWindow;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.crowdcoin.mainBoard.table.Tab;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;

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
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata");
        SQLTable table2 = new SQLTable(SQLData.getSqlConnection(),"coindata");

        // Dummy field action handler
        InteractiveFieldActionEvent dummyEvent = new InteractiveFieldActionEvent() {
            @Override
            public void fieldActionHandler(ActionEvent event, Control field, InteractivePane pane) {
                return;
            }
        };

        // Test Tab 1
        Tab testTab = new Tab(model,table,"testTab");
        testTab.setTabTableAction(new TabActionEvent() {
            @Override
            public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane) {
                System.out.println("This tab was pressed!");
            }
        });

        // an InteractiveTabPane houses all data for a specific tab in regard to the rightDisplay and buttonGrid
        InteractiveTabPane testPane = testTab.getInteractivePane();
        // Add fields to the pane
        testPane.addField("This is a test", "This is a test combo object for textfield combo's 1",dummyEvent);
        testPane.addField("This is a test2", "This is a test combo object for textfield combo's 2", dummyEvent);
        testPane.addField("This is a test3", "This is a test combo object for textfield combo's 3", dummyEvent);

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

        // Test Tab 2
        Tab testTab2 = new Tab(model,table2,"testTab2");
        testTab2.setTabTableAction(new TabActionEvent() {
            @Override
            public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane) {
                System.out.println("This tab 2 was pressed!");
            }
        });

        // an InteractiveTabPane houses all data for a specific tab in regard to the rightDisplay and buttonGrid
        InteractiveTabPane testPane2 = testTab2.getInteractivePane();
        // Add fields to the pane
        testPane2.addField("This is a test", "This is a test combo object for textfield combo's 1", dummyEvent);
        testPane2.addField("This is a test2", "This is a test combo object for textfield combo's 2", dummyEvent);
        testPane2.addField("This is a test3", "This is a test combo object for textfield combo's 3", dummyEvent);
        testPane2.addField("This is a test4", "This is a test combo object for textfield combo's 3", dummyEvent);
        // Add choice field
        testPane2.addChoiceField("This is a choice field", "This is a test combo object for textfield combo's 4", dummyEvent,"Option1","Option2","Option3");

        // Testing buttons
        // One can specify how they would like a button to handle an ActionEvent by defining it within a specific class or on the fly
        InteractiveButtonActionEvent testHandler2 = new InteractiveButtonActionEvent() {
            @Override
            public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
                System.out.println(event.getEventType().getName());
                System.out.println(button.getText() + " fired an event!");
            }
        };

        testPane2.addButton("Button1",testHandler2);
        testPane2.addButton("Button2", testHandler2);
        testPane2.addButton("Button3", testHandler2);

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
                PopWindow window = new PopWindow("New Entry",table);
                window.getWindowPane().addButton("Window button", (test2,test3,test4)-> System.out.println("Hello Window!"));
                window.getWindowPane().addButton("Window button2", (test2,test3,test4)-> {System.out.println("Hello Window!");
                    InteractiveWindowPane testWindowPane = (InteractiveWindowPane) test4;
                    System.out.println(Arrays.toString(testWindowPane.getAllInput().toArray()));
                });
                window.getWindowPane().addChoiceField("Test","Testing window choice field",dummyEvent,"Option1","Option2","Option3");
                window.getWindowPane().addField("Test","This is a field test",dummyEvent);
                window.start(new Stage());
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
