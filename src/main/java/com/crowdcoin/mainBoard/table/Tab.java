package com.crowdcoin.mainBoard.table;

import com.crowdcoin.exceptions.modelClass.NotZeroArgumentException;
import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.tab.IncompatibleModelClassException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.networking.sqlcom.data.SQLTable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

public class Tab {

    private TableInformation tableInfo;
    private ModelClass modelClass;
    private ModelClassFactory factory;
    private SQLTable sqlTable;

    private int defaultNumberOfRows = 10;
    private double totalWidth;

    /**
     * Create a Tab object. Similar to a tab in a web browser, a Tab object stores a "state" of the TableView
     * @param classToModel an instance of the class to model for column data. This class will be used to get values for columns. To learn more, please see ModelClass and ModelClassFactory
     * @param sqlTable an SQLTable object which is responsible for gathering data of a specific table found within the database
     * @throws NotZeroArgumentException if the model class contains invokable methods that have more than zero parameters
     * @throws IncompatibleModelClassException if the model class does not contain the same number of invokable methods as there are columns within the given table within the database (as specified within the SQLTable object)
     */
    public Tab(Object classToModel, SQLTable sqlTable) throws NotZeroArgumentException, IncompatibleModelClassException {

        // Create instances needed
        this.tableInfo = new TableInformation();
        this.factory = new ModelClassFactory();
        this.sqlTable = sqlTable;
        // Build model class from model reference
        this.modelClass = this.factory.build(classToModel);

        // Check that the number of methods within the model reference are the same as the number of columns within the sql database table
        if (this.sqlTable.getNumberOfColumns() != this.modelClass.getNumberOfMethods()) {
            throw new IncompatibleModelClassException(this.modelClass.getNumberOfMethods(),this.sqlTable.getNumberOfColumns());
        }

        setupTab();

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

    /**
     * Load a tab into a TableView object
     * @param destinationTable the TableView object to apply the Tab object to. ALL present data within the TableView object will be erased and replaced with the data found within the Tab object
     * @throws FailedQueryException
     * @throws SQLException
     * @throws InvalidRangeException
     * @throws NotZeroArgumentException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public void loadTab(TableView destinationTable) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

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

    }

}
