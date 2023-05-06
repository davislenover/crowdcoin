package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassException;
import com.crowdcoin.exceptions.tab.ModelClassConstructorTypeException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class Tab {

    private TableInformation tableInfo;
    private ModelClass modelClass;
    private ModelClassFactory factory;
    private SQLTable sqlTable;
    private InteractivePane interactivePane;

    // TabTableActionEvent is intended to allow arbitrary logic to be invoked when a user selects anything within the TableView object within the Tab
    // Basically provides a connection between the TableView and InteractivePane on the right beside the table (something happens in the Table, do something in the InteractivePane)
    // On instantiation, set action event to default
    private TabTableActionEvent tableSelectHandler = new TabTableActionEvent() {
        @Override
        public void tableActionHandler(TableInformation tableInformation, InteractivePane pane) {
            return;
        }
    };;


    private int defaultNumberOfRows = 10;
    private double totalWidth;

    /**
     * Create a Tab object. Similar to a tab in a web browser, a Tab object stores a "state" of the TableView
     * @param classToModel an instance of the class to model for column data. This class will be used to get values for columns. To learn more, please see ModelClass and ModelClassFactory
     * @param sqlTable an SQLTable object which is responsible for gathering data of a specific table found within the database
     * @throws NotZeroArgumentException if the model class contains invokable methods that have more than zero parameters
     * @throws IncompatibleModelClassException if the model class does not contain the same number of invokable methods as there are columns within the given table within the database (as specified within the SQLTable object)
     * @throws ModelClassConstructorTypeException if the modelClass constructor contains an argument type mismatch to one or more columns within the database table. This could mean the constructor arguments of the modeling class are not in the correct order. Note that SQLTable returns a list where each element is a row of the database table sorted in ordinal position thus it is imperative to organize constructor parameters in the same position as column ordinal position
     */
    public Tab(Object classToModel, SQLTable sqlTable, InteractivePane interactivePane) throws NotZeroArgumentException, IncompatibleModelClassException, ModelClassConstructorTypeException {

        // Create instances needed
        this.tableInfo = new TableInformation();
        this.factory = new ModelClassFactory();
        this.sqlTable = sqlTable;
        this.interactivePane = interactivePane;
        // Build model class from model reference
        this.modelClass = this.factory.build(classToModel);

        // Check that the number of methods within the model reference are the same as the number of columns within the sql database table
        if (this.sqlTable.getNumberOfColumns() != this.modelClass.getNumberOfMethods()) {
            throw new IncompatibleModelClassException(this.modelClass.getNumberOfMethods(),this.sqlTable.getNumberOfColumns());
        }

        if (!checkTypes()) {
            throw new ModelClassConstructorTypeException();
        }

        setupTab();

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

    // Method to set up TableInformation object with columns
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

            this.tableInfo.addColumn(columnObject);

        }

    }

    public void setTabTableAction(TabTableActionEvent event) {
        this.tableSelectHandler = event;
    }

    /**
     * Load a tab into a TableView object
     * @param destinationTable the TableView object to apply the Tab object to. ALL present data within the TableView object will be erased and replaced with the data found within the Tab object
     * @param fieldPane apart of the intractableDisplay, fieldPane indicates the GridPane where user input fields are to be placed
     * @param buttonPane apart of the intractableDisplay, buttonPane indicates the GridPane where user intractable buttons are to be placed
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void loadTab(TableView destinationTable, GridPane fieldPane, GridPane buttonPane) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data and columns
        destinationTable.getItems().clear();
        destinationTable.getColumns().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Load columns within Tab to table
        for (TableColumn column : this.tableInfo) {
            destinationTable.getColumns().add(column);
        }

        // Load data within Tab to table
        // Get default starting data
        List<List<Object>> rows = this.sqlTable.getRows(0,this.defaultNumberOfRows,0,this.sqlTable.getNumberOfColumns()-1);
        for (List<Object> row : rows) {
            // Basically, each table column doesn't know how to get data to display it other than that it is using a ModelClass type as input and Object type as output
            // How to get data is specified by setting the cell value factory of each column, where in this case, it is set to get the ModelClass object and invoke the corresponding method
            // Note that it is NOT a specific ModelClass, it simply specifies that once it receives a specific instance, it will do what it was told to do with any other ModelClass
            // The corresponding method is at the same index as the current size of the column data list (at the time) thus if a ModelClass has 4 invokable methods, the first column will get data from method 1
            // second from method 2 and so on
            // Thus when we add a ModelClass here, to display the data we are simply invoking the corresponding method within the specific instance of the ModelClass at the given index
            // This cloned ModelClass houses the row data from the table within the database and utilizes the specified methods (via @TableReadable annotation) to retrieve the data
            destinationTable.getItems().add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

        // Clear prior mouse clicked event before applying current Tab one (redundant but safer)
        destinationTable.setOnMouseClicked(null);

        // Change MouseCLicked event to the TableView object, to invoke the corresponding tableActionHandler method
        destinationTable.setOnMouseClicked(mouseEvent -> this.tableSelectHandler.tableActionHandler(this.tableInfo,this.interactivePane));

        // Apply InteractivePane
        this.interactivePane.applyInteractivePane(fieldPane,buttonPane);

    }

}
