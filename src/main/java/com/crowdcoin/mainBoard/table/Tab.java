package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassException;
import com.crowdcoin.exceptions.tab.ModelClassConstructorTypeException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Tab {

    private ColumnContainer columnContainer;
    private ModelClass modelClass;
    private ModelClassFactory factory;
    private TableViewManager tableViewManager;
    private SQLTable sqlTable;
    private InteractivePane interactivePane;
    private String tabID;

    // TabActionEvent is intended to allow arbitrary logic to be invoked when a user selects anything within the TableView object within the Tab
    // Basically provides a connection between the TableView and InteractivePane on the right beside the table (something happens in the Table, do something in the InteractivePane)
    // On instantiation, set action event to default
    private TabActionEvent tableSelectHandler = new TabActionEvent() {
        @Override
        public void tableActionHandler(ColumnContainer columnContainer, InteractivePane pane) {
            return;
        }
    };;


    private int defaultNumberOfRows = 10;
    private double totalWidth;

    /**
     * Create a Tab object. Similar to a tab in a web browser, a Tab object stores a "state" of the TableView. Upon creation, a blank InteractivePane is available for use
     * @param classToModel an instance of the class to model for column data. This class will be used to get values for columns. To learn more, please see ModelClass and ModelClassFactory
     * @param sqlTable an SQLTable object which is responsible for gathering data of a specific table found within the database
     * @param tabID a String identifier for the tab instance
     * @throws NotZeroArgumentException if the model class contains invokable methods that have more than zero parameters
     * @throws IncompatibleModelClassException if the model class does not contain the same number of invokable methods as there are columns within the given table within the database (as specified within the SQLTable object)
     * @throws ModelClassConstructorTypeException if the modelClass constructor contains an argument type mismatch to one or more columns within the database table. This could mean the constructor arguments of the modeling class are not in the correct order. Note that SQLTable returns a list where each element is a row of the database table sorted in ordinal position thus it is imperative to organize constructor parameters in the same position as column ordinal position
     */
    public Tab(Object classToModel, SQLTable sqlTable, String tabID) throws NotZeroArgumentException, IncompatibleModelClassException, ModelClassConstructorTypeException, FailedQueryException, SQLException, InvalidRangeException {

        // Create instances needed
        this.columnContainer = new ColumnContainer();
        this.factory = new ModelClassFactory();
        this.sqlTable = sqlTable;
        this.interactivePane = new InteractivePane();
        // Build model class from model reference
        this.modelClass = this.factory.build(classToModel);

        // Check that the number of methods within the model reference are the same as the number of columns within the sql database table
        if (this.sqlTable.getNumberOfColumns() != this.modelClass.getNumberOfMethods()) {
            throw new IncompatibleModelClassException(this.modelClass.getNumberOfMethods(),this.sqlTable.getNumberOfColumns());
        }

        if (!checkTypes()) {
            throw new ModelClassConstructorTypeException();
        }

        this.tableViewManager = new TableViewManager(this.sqlTable,this.columnContainer,this.modelClass,this.factory);

        setupTab();

        this.tabID = tabID;

    }

    // Method to check if the parameter types of the modelClass constructor match that of columns within the database
    // False if there is a mismatch
    private boolean checkTypes() {

        // Get constructor of modelClass (which is assumed to be the only constructor)
        Class<?>[] modelClassConstructorTypes = this.modelClass.getInstanceClass().getConstructors()[0].getParameterTypes();

        // Loop through the column types
        int constructorTypeIndex = 0;
        for (String columnType : this.sqlTable.getColumnTypes()) {

            try {
                // Queries class contains a hashmap that provides the corresponding class for the given column type in sql
                // Check if both the columnType class and the corresponding constructor parameter match
                if (!SQLDefaultQueries.SQLToJavaType.get(columnType.toUpperCase()).getName().toUpperCase().contains(modelClassConstructorTypes[constructorTypeIndex].getName().toUpperCase())) {
                    return false;
                }
                // Index out of bounds also indicates there are more columns than there are arguments in the constructor
            } catch (IndexOutOfBoundsException e) {
                return false;
            }
            constructorTypeIndex++;
        }

        return true;

    }

    // Method to set up ColumnContainer object with columns
    private void setupTab() {

        this.totalWidth = 0;

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
     * Gets the associated InteractivePane created upon instantiation of a Tab object.
     * @return the associated InteractivePane object. The object is live.
     */
    public InteractivePane getInteractivePane() {
        return this.interactivePane;
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
    public void loadTab(TableView destinationTable, GridPane fieldPane, GridPane buttonPane, Button previous, Button next) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();
        destinationTable.getColumns().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        // Set number of visible rows
        //destinationTable.setFixedCellSize(this.defaultNumberOfRows);

        // Load data within Tab to table
        this.tableViewManager.applyCurrentRows(destinationTable);

        // Clear prior mouse clicked event before applying current Tab one (redundant but safer)
        destinationTable.setOnMouseClicked(null);

        // Change MouseCLicked event to the TableView object, to invoke the corresponding tableActionHandler method
        destinationTable.setOnMouseClicked(mouseEvent -> this.tableSelectHandler.tableActionHandler(this.columnContainer,this.interactivePane));

        // Set previous and next button functionalities
        previous.setOnAction(actionEvent -> {
            try {
                this.tableViewManager.applyPreviousRows(destinationTable);
            } catch (NotZeroArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        next.setOnAction(actionEvent -> {
            try {
                this.tableViewManager.applyNextRows(destinationTable);
            } catch (NotZeroArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        // Apply InteractivePane
        this.interactivePane.applyInteractivePane(fieldPane, buttonPane);

    }

    /**
     * Get the tabID
     * @return the tabID as a String
     */
    public String getTabID() {
        return tabID;
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
}
