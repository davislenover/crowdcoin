package com.crowdcoin.mainBoard;

import com.crowdcoin.FXTools.StageManager;
import com.crowdcoin.format.defaultActions.interactive.FieldActionDummyEvent;
import com.crowdcoin.mainBoard.Interactive.*;
import com.crowdcoin.mainBoard.Interactive.input.InputField;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveChoiceBox;
import com.crowdcoin.mainBoard.Interactive.input.InteractiveTextField;
import com.crowdcoin.mainBoard.Interactive.submit.InteractiveButton;
import com.crowdcoin.mainBoard.Interactive.submit.SubmitField;
import com.crowdcoin.mainBoard.table.*;
import com.crowdcoin.mainBoard.toolBar.MenuGroup;
import com.crowdcoin.mainBoard.toolBar.MenuGroupContainer;
import com.crowdcoin.mainBoard.toolBar.MenuOption;
import com.crowdcoin.mainBoard.window.NewEntryPopWindow;
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
        ModelClass modelClass = new ModelClassFactory().build(model);
        SQLTable table = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());
        SQLTable table2 = new SQLTable(SQLData.getSqlConnection(),"coindata",modelClass.getColumns());

        // Test Tab 1
        Tab testTab = new Tab(model,table,"testTab");
        testTab.setTabTableAction(new TabActionEvent() {
            @Override
            public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane) {

                try {
                    for (Object data : columnContainer.getSelectedRow()) {
                        System.out.println(data.toString());
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });

        // an InteractiveTabPane houses all data for a specific tab in regard to the rightDisplay and buttonGrid
        InteractiveTabPane testPane = testTab.getInteractivePane();
        // Add fields to the pane
        testPane.addInputField(new InteractiveTextField("This is a test","This is a test combo object for textfield combo's 1",new FieldActionDummyEvent()));
        testPane.addInputField(new InteractiveTextField("This is a test2","This is a test combo object for textfield combo's 2",new FieldActionDummyEvent()));
        testPane.addInputField(new InteractiveTextField("This is a test3","This is a test combo object for textfield combo's 3",new FieldActionDummyEvent()));

        // Testing buttons
        // One can specify how they would like a button to handle an ActionEvent by defining it within a specific class or on the fly
        InteractiveButtonActionEvent testHandler = new InteractiveButtonActionEvent() {
            @Override
            public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
                System.out.println(event.getEventType().getName());
                System.out.println(button.getText() + " fired an event!");
            }
        };

        SubmitField button1 = new InteractiveButton("Button1",testHandler);
        button1.setOrder(0);
        SubmitField button2 = new InteractiveButton("Button2",testHandler);
        button2.setOrder(1);
        testPane.addSubmitField(button1);
        testPane.addSubmitField(button2);

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
        testPane2.addInputField(new InteractiveTextField("This is a test1","This is a test combo object for textfield combo's 1",new FieldActionDummyEvent()));
        testPane2.addInputField(new InteractiveTextField("This is a test2","This is a test combo object for textfield combo's 2",new FieldActionDummyEvent()));
        testPane2.addInputField(new InteractiveTextField("This is a test3","This is a test combo object for textfield combo's 3",new FieldActionDummyEvent()));
        testPane2.addInputField(new InteractiveTextField("This is a test4","This is a test combo object for textfield combo's 4",new FieldActionDummyEvent()));
        // Add choice field
        InteractiveChoiceBox box = new InteractiveChoiceBox("This is a choice field","This is a test combo object for textfield combo's 4",new FieldActionDummyEvent());
        box.addValue("Option1");
        box.addValue("Option2");
        box.addValue("Option3");
        testPane2.addInputField(box);

        // Testing buttons
        // One can specify how they would like a button to handle an ActionEvent by defining it within a specific class or on the fly
        InteractiveButtonActionEvent testHandler2 = new InteractiveButtonActionEvent() {
            @Override
            public void buttonActionHandler(ActionEvent event, Button button, InteractivePane pane) {
                System.out.println(event.getEventType().getName());
                System.out.println(button.getText() + " fired an event!");
            }
        };

        SubmitField button2_1 = new InteractiveButton("Button1",testHandler2);
        button2_1.setOrder(0);
        SubmitField button2_2 = new InteractiveButton("Button2",testHandler2);
        button2_2.setOrder(1);
        SubmitField button2_3 = new InteractiveButton("Button3",testHandler2);
        button2_3.setOrder(2);
        testPane2.addSubmitField(button2_1);
        testPane2.addSubmitField(button2_2);
        testPane2.addSubmitField(button2_3);

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
