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
    private SQLTable table;

    private int defaultNumberOfRows = 10;
    private double totalWidth;

    public Tab(Object modelClass, SQLTable table) throws NotZeroArgumentException, IncompatibleModelClassException {
        this.tableInfo = new TableInformation();
        this.factory = new ModelClassFactory();
        this.table = table;
        this.modelClass = this.factory.build(modelClass);

        if (this.table.getNumberOfColumns() != this.modelClass.getNumberOfMethods()) {
            throw new IncompatibleModelClassException(this.modelClass.getNumberOfMethods(),this.table.getNumberOfColumns());
        }

        setupTab();

    }

    private void setupTab() {

        this.totalWidth = 0;

        List<String> columnNames = this.table.getColumnNames();

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

    public void loadTab(TableView destinationTable) throws FailedQueryException, SQLException, InvalidRangeException, NotZeroArgumentException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        // Clear current data
        destinationTable.getItems().clear();

        destinationTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        // Load columns
        for (TableColumn column : this.tableInfo) {
            destinationTable.getColumns().add(column);
        }

        // Load data
        // Get default starting data
        List<List<Object>> rows = this.table.getRows(0,this.defaultNumberOfRows,0,this.table.getNumberOfColumns()-1);
        for (List<Object> row : rows) {
            destinationTable.getItems().add(this.factory.buildClone(this.modelClass,row.toArray()));
        }

    }

}
