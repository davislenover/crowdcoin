package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassMethodNamesException;
import com.crowdcoin.exceptions.tab.ModelClassConstructorTypeException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.mainBoard.Interactive.InteractiveTabPane;
import com.crowdcoin.mainBoard.table.Observe.*;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import com.crowdcoin.networking.sqlcom.data.filter.FilterController;
import javafx.scene.control.Button;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Note Tabs are observable as they can change things that other classes may need to react to, such as application of new filters
public class Tab implements Observable<ModifyEvent>, Observer<ModifyEvent> {

    private ColumnContainer columnContainer;
    private ModelClass modelClass;
    private ModelClassFactory factory;
    private TableViewManager tableViewManager;
    private SQLTable sqlTable;
    private InteractiveTabPane interactiveTabPane;
    private FilterController filterController;
    private String tabID;

    // TabActionEvent is intended to allow arbitrary logic to be invoked when a user selects anything within the TableView object within the Tab
    // Basically provides a connection between the TableView and InteractiveTabPane on the right beside the table (something happens in the Table, do something in the InteractiveTabPane)
    // On instantiation, set action event to default
    private TabActionEvent tableSelectHandler = new TabActionEvent() {
        @Override
        public void tableActionHandler(ColumnContainer columnContainer, InteractiveTabPane pane, SQLTable table) {
            return;
        }
    };;


    private int defaultNumberOfRows = 10;
    private double totalWidth;

    // Observer list
    private List<Observer<ModifyEvent>> subscriptionList;

    /**
     * Create a Tab object. Similar to a tab in a web browser, a Tab object stores a "state" of the TableView. Upon creation, a blank InteractiveTabPane is available for use
     * @param classToModel an instance of the class to model for column data. This class will be used to get values for columns. To learn more, please see ModelClass and ModelClassFactory
     * @param sqlTable an SQLTable object which is responsible for gathering data of a specific table found within the database
     * @param tabID a String identifier for the tab instance
     * @throws NotZeroArgumentException if the model class contains invokable methods that have more than zero parameters
     * @throws IncompatibleModelClassException if the model class does not contain the same number of invokable methods as there are columns within the given table within the database (as specified within the SQLTable object)
     * @throws ModelClassConstructorTypeException if the modelClass constructor contains an argument type mismatch to one or more columns within the database table. This could mean the constructor arguments of the modeling class are not in the correct order. Note that SQLTable returns a list where each element is a row of the database table sorted in ordinal position thus it is imperative to organize constructor parameters in the same position as column ordinal position
     */
    public Tab(Object classToModel, SQLTable sqlTable, String tabID) throws NotZeroArgumentException, IncompatibleModelClassException, ModelClassConstructorTypeException, FailedQueryException, SQLException, InvalidRangeException, IncompatibleModelClassMethodNamesException {

        // Create instances needed
        this.columnContainer = new ColumnContainer();
        this.factory = new ModelClassFactory();
        this.sqlTable = sqlTable;
        this.filterController = new FilterController();
        this.interactiveTabPane = new InteractiveTabPane();
        // Set Tab to observe pane (if some class triggers the notify method to indicate changes were made to the pane)
        this.interactiveTabPane.addObserver(this);
        // Build model class from model reference
        this.modelClass = this.factory.build(classToModel);

        // Check validity of modelClass
        ModelClassFactory.checkModelClassValidity(this.modelClass,this.sqlTable);

        this.tableViewManager = new TableViewManager(this.sqlTable,this.columnContainer,this.modelClass,this.factory);

        setupTab();

        this.tabID = tabID;

        // The "subscription list" will be defined by an ArrayList
        this.subscriptionList = new ArrayList<>();

        // Tab will observe the filter controller for changes to filters (such as if a filter is being added)
        this.filterController.addObserver(this);
        // Observe SQL Table for any other changes
        this.sqlTable.addObserver(this);

    }

    // Method to set up ColumnContainer object with columns
    private void setupTab() {

        this.totalWidth = 0;

        // getColumnNames() only returns columns that have Readable permissions
        List<String> columnNames = this.sqlTable.getColumnNames();

        // Loop through each column
        for (String columnName : columnNames) {

            // Create a new column with the specified name
            TableColumn<ModelClass,Object> columnObject = new TableColumn<>(columnName);
            columnObject.setId(columnName);
            columnObject.setReorderable(false);

            // Get the text of the new column and set it's width accordingly
            Text columnText = new Text(columnObject.getText());
            columnText.setFont(columnObject.getCellFactory().call(columnObject).getFont());

            double widthValue = columnText.prefWidth(-1)+columnText.getText().length();
            columnObject.setMinWidth(widthValue);
            columnObject.setPrefWidth(widthValue);
            this.totalWidth+=widthValue;

            this.columnContainer.addColumn(columnObject);


        }

    }

    /**
     * Gets the associated InteractiveTabPane created upon instantiation of a Tab object.
     * @return the associated InteractiveTabPane object. The object is live.
     */
    public InteractiveTabPane getInteractivePane() {
        return this.interactiveTabPane;
    }

    /**
     * Set the action to be performed when a user clicks within the TableView object. Note that one needs to load the tab to apply the corresponding event
     * @param event the arbitrary logic to invoke
     */
    public void setTabTableAction(TabActionEvent event) {
        this.tableSelectHandler = event;
    }

    /**
     * Load a tab into a TableView object
     * @param destinationTable the TableView object to apply the Tab object to. ALL present data within the TableView object (and any MouseClick On Mouse Pressed event) will be erased and replaced with the data found within the Tab object
     * @param fieldPane apart of the IntractablePane, fieldPane indicates the GridPane where user input fields are to be placed
     * @param buttonPane apart of the IntractablePane, buttonPane indicates the GridPane where user intractable buttons are to be placed
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void loadTab(TableView destinationTable, GridPane fieldPane, GridPane buttonPane, Button previous, Button next, SplitMenuButton filterButton) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();
        destinationTable.getColumns().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Load data within Tab to table
        // Note that TableViewManager gets raw rows, meaning it ignores column permissions BUT it relies on columnContainer to retrieve which columns will be added to the table and that container contains columns only with the correct permissions
        this.tableViewManager.applyCurrentRows(destinationTable);

        // Clear prior mouse clicked event before applying current Tab one (redundant but safer)
        destinationTable.setOnMouseClicked(null);

        // Change MouseCLicked event to the TableView object, to invoke the corresponding tableActionHandler method
        destinationTable.setOnMouseClicked(mouseEvent -> this.tableSelectHandler.tableActionHandler(this.columnContainer,this.interactiveTabPane,this.sqlTable));

        // Set previous and forward button logic for TableViewManager instance
        this.tableViewManager.applyPrevNextButtons(destinationTable,previous,next);

        // Apply InteractiveTabPane
        this.interactiveTabPane.applyInteractivePane(fieldPane, buttonPane);

        // Apply filters
        this.filterController.applyFilters(filterButton,this.sqlTable.getFilterManager(),this.sqlTable);

    }

    @Override
    public void removeObserving() {
        this.sqlTable.removeObserver(this);
        this.filterController.removeObserver(this);
    }

    /**
     * Get the tabID
     * @return the tabID as a String
     */
    public String getTabID() {
        return tabID;
    }

    /**
     * Gets the SQL table name (NOT Object) that the tab currently represents
     * @return
     */
    public String getSQLTableName() {
        return this.sqlTable.getTableName();
    }

    /**
     * Change the default number of rows that will be displayed in the TableView once the Tab is loaded
     * @param numOfRows the new default number of rows as an integer
     * @throws IllegalArgumentException if the default number is less than or equal to 0
     */
    public void changeDefaultRowView(int numOfRows) {

        if (numOfRows > 0) {
            this.defaultNumberOfRows = numOfRows;
        } else {
            throw new IllegalArgumentException("numOfRows must be greater than 0 (entered: " + numOfRows + ")");
        }

    }

    /**
     * Resets Table View. This will refresh the SQL data.
     */
    public void resetTableView() {
        // Reset TableView manager, this will cause it to grab a fresh query (with any Filters that have now been applied)
        try {
            this.tableViewManager.reset();
        } catch (Exception e) {
            // TODO Error handling
        }
    }

    @Override
    public boolean addObserver(Observer<ModifyEvent> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ModifyEvent> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(ModifyEvent event) {

        if (event.getEventType() != ModifyEventType.NEW_ROW && event.getEventType() != ModifyEventType.ROW_MODIFIED) {
            event.setEventData(this.tabID);
        }

        for (Observer<ModifyEvent> observer : this.subscriptionList) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }

    // Tab will watch for changes to the SQLTable (mainly for Filter changes)
    @Override
    public void update(ModifyEvent passThroughObject) {

        if (passThroughObject.getEventType() != ModifyEventType.PANE_UPDATE) {
            // Reset TableView manager
            this.resetTableView();
        }
        // Notify all Tab observers that this tab updated it's SQLTable
        this.notifyObservers(passThroughObject);
    }
}
